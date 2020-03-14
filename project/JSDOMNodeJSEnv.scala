package tempfix

import java.io._
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardCopyOption}

import org.scalajs.jsenv.JSUtils.escapeJS
import org.scalajs.jsenv._
import org.scalajs.jsenv.nodejs._
import sbt._

import scala.util.control.NonFatal

// @Warning https://github.com/scalacenter/scalajs-bundler/issues/332#issuecomment-594401804

// HACK Copy of Scala.js’ JSDOMNodeJSEnv. The only change is the ability to pass the directory in which jsdom has been installed
class JSDOMNodeJSEnv(config: JSDOMNodeJSEnv.Config) extends JSEnv {

  val name: String = "Node.js with JSDOM"

  def start(input: Seq[Input], runConfig: RunConfig): JSRun = {
    JSDOMNodeJSEnv.validator.validate(runConfig)
    val scripts = validateInput(input)
    try {
      internalStart(codeWithJSDOMContext(scripts), runConfig)
    } catch {
      case NonFatal(t) =>
        JSRun.failed(t)
    }
  }

  def startWithCom(input: Seq[Input], runConfig: RunConfig,
    onMessage: String => Unit): JSComRun = {
    JSDOMNodeJSEnv.validator.validate(runConfig)
    val scripts = validateInput(input)
    ComRun.start(runConfig, onMessage) { comLoader =>
      internalStart(comLoader :: codeWithJSDOMContext(scripts), runConfig)
    }
  }

  private def validateInput(input: Seq[Input]): List[Path] = {
    input.map {
      case Input.Script(script) =>
        script

      case _ =>
        throw new UnsupportedInputException(input)
    }.toList
  }

  private def internalStart(files: List[Path], runConfig: RunConfig): JSRun = {
    val command = config.executable :: config.args
    val externalConfig = ExternalJSRun.Config()
      .withEnv(env)
      .withRunConfig(runConfig)
    ExternalJSRun.start(command, externalConfig)(JSDOMNodeJSEnv.write(files))
  }

  private def env: Map[String, String] =
    Map("NODE_MODULE_CONTEXTS" -> "0") ++ config.env

  private def codeWithJSDOMContext(scripts: List[Path]): List[Path] = {
    val scriptsURIs = scripts.map(JSDOMNodeJSEnv.materialize(_))
    val scriptsURIsAsJSStrings =
      scriptsURIs.map(uri => "\"" + escapeJS(uri.toASCIIString) + "\"")
    val scriptsURIsJSArray = scriptsURIsAsJSStrings.mkString("[", ", ", "]")
    val jsDOMCode = {
      s"""
         |
         |(function () {
         |  var jsdom = require("jsdom");
         |
         |  if (typeof jsdom.JSDOM === "function") {
         |    // jsdom >= 10.0.0
         |    var virtualConsole = new jsdom.VirtualConsole()
         |      .sendTo(console, { omitJSDOMErrors: true });
         |    virtualConsole.on("jsdomError", function (error) {
         |      try {
         |        // Display as much info about the error as possible
         |        if (error.detail && error.detail.stack) {
         |          console.error("" + error.detail);
         |          console.error(error.detail.stack);
         |        } else {
         |          console.error(error);
         |        }
         |      } finally {
         |        // Whatever happens, kill the process so that the run fails
         |        process.exit(1);
         |      }
         |    });
         |
         |    var dom = new jsdom.JSDOM("", {
         |      virtualConsole: virtualConsole,
         |      url: "http://localhost/",
         |
         |      /* Allow unrestricted <script> tags. This is exactly as
         |       * "dangerous" as the arbitrary execution of script files we
         |       * do in the non-jsdom Node.js env.
         |       */
         |      resources: "usable",
         |      runScripts: "dangerously"
         |    });
         |
         |    var window = dom.window;
         |    window["scalajsCom"] = global.scalajsCom;
         |
         |    var scriptsSrcs = $scriptsURIsJSArray;
         |    for (var i = 0; i < scriptsSrcs.length; i++) {
         |      var script = window.document.createElement("script");
         |      script.src = scriptsSrcs[i];
         |      window.document.body.appendChild(script);
         |    }
         |  } else {
         |    // jsdom v9.x
         |    var virtualConsole = jsdom.createVirtualConsole()
         |      .sendTo(console, { omitJsdomErrors: true });
         |    virtualConsole.on("jsdomError", function (error) {
         |      /* This inelegant if + console.error is the only way I found
         |       * to make sure the stack trace of the original error is
         |       * printed out.
         |       */
         |      if (error.detail && error.detail.stack)
         |        console.error(error.detail.stack);
         |
         |      // Throw the error anew to make sure the whole execution fails
         |      throw error;
         |    });
         |
         |    jsdom.env({
         |      html: "",
         |      virtualConsole: virtualConsole,
         |      url: "http://localhost/",
         |      created: function (error, window) {
         |        if (error == null) {
         |          window["scalajsCom"] = global.scalajsCom;
         |        } else {
         |          throw error;
         |        }
         |      },
         |      scripts: $scriptsURIsJSArray
         |    });
         |  }
         |})();
         |""".stripMargin
    }

    val codeFile = config.jsDomDirectory / "codeWithJSDOMContext.js"
    IO.write(codeFile, jsDOMCode)
    List(codeFile.toPath())
  }
}

object JSDOMNodeJSEnv {
  private lazy val validator = ExternalJSRun.supports(RunConfig.Validator())

  /* Unlike NodeJSEnv.scala upstream, we use `require` to load files on-disk
   * rather than `runInThisContext`. We do this to preserve the enclosing
   * directory of the scripts, which is necessary for Node.js to find npm
   * packages in the directory hierarchy.
   *
   * We can do this in `JSDOMNodeJSEnv` because all the `files` are under our
   * control, and we know that none of them declares `var`s at the top-level
   * that would need to be accessed from other scripts.
   */
  private def write(files: List[Path])(out: OutputStream): Unit = {
    val p = new PrintStream(out, false, "UTF8")
    try {
      def writeRunScript(path: Path): Unit = {
        try {
          val f = path.toFile
          val pathJS = "\"" + escapeJS(f.getAbsolutePath) + "\""
          p.println(s"""require($pathJS);""")
        } catch {
          case _: UnsupportedOperationException =>
            val code = new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
            val codeJS = "\"" + escapeJS(code) + "\""
            val pathJS = "\"" + escapeJS(path.toString) + "\""
            p.println(s"""
              require('vm').runInThisContext(
                $codeJS,
                { filename: $pathJS, displayErrors: true }
              );
            """)
        }
      }

      for (file <- files)
        writeRunScript(file)
    } finally {
      p.close()
    }
  }

  // tmpSuffixRE and tmpFile copied from HTMLRunnerBuilder.scala in Scala.js

  private val tmpSuffixRE = """[a-zA-Z0-9-_.]*$""".r

  private def tmpFile(path: String, in: InputStream): URI = {
    try {
      /* - createTempFile requires a prefix of at least 3 chars
       * - we use a safe part of the path as suffix so the extension stays (some
       *   browsers need that) and there is a clue which file it came from.
       */
      val suffix = tmpSuffixRE.findFirstIn(path).orNull

      val f = File.createTempFile("tmp-", suffix)
      f.deleteOnExit()
      Files.copy(in, f.toPath(), StandardCopyOption.REPLACE_EXISTING)
      f.toURI()
    } finally {
      in.close()
    }
  }

  private def materialize(path: Path): URI = {
    try {
      path.toFile.toURI
    } catch {
      case _: UnsupportedOperationException =>
        tmpFile(path.toString, Files.newInputStream(path))
    }
  }

  final class Config private (
    val jsDomDirectory: File,
    val executable: String,
    val args: List[String],
    val env: Map[String, String]
  ) {
    private def this(jsDomDirectory: File) = {
      this(
        jsDomDirectory,
        executable = "node",
        args = Nil,
        env = Map.empty
      )
    }

    def withExecutable(executable: String): Config =
      copy(executable = executable)

    def withArgs(args: List[String]): Config =
      copy(args = args)

    def withEnv(env: Map[String, String]): Config =
      copy(env = env)

    private def copy(
      executable: String = executable,
      args: List[String] = args,
      env: Map[String, String] = env
    ): Config = {
      new Config(jsDomDirectory, executable, args, env)
    }
  }

  object Config {
    /** Returns a default configuration for a [[JSDOMNodeJSEnv]].
      *
      *  The defaults are:
      *
      *  - `executable`: `"node"`
      *  - `args`: `Nil`
      *  - `env`: `Map.empty`
      */
    def apply(jsDomDirectory: File): Config = new Config(jsDomDirectory)
  }
}
