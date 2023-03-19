const React = require('react');

const CompLibrary = require('../../core/CompLibrary.js');

// const MarkdownBlock = CompLibrary.MarkdownBlock; /* Used to read markdown */
const Container = CompLibrary.Container;
const GridBlock = CompLibrary.GridBlock;

class HomeSplash extends React.Component {
  render () {
    const {siteConfig, language = ''} = this.props;
    const {baseUrl, docsUrl} = siteConfig;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
    const langPart = `${language ? `${language}/` : ''}`;
    const docUrl = (doc) => `${baseUrl}${docsPart}${langPart}${doc}`;

    const SplashContainer = (props) => (
      <div className="homeContainer">
        <div className="homeSplashFade">
          <div className="wrapper homeWrapper">{props.children}</div>
        </div>
      </div>
    );

    const Logo = ({img_src}) => (
      <div className="projectLogo">
        <img src={img_src} alt="Project Logo"/>
      </div>
    );

    const ProjectTitle = ({title, tagline}) => (
      <h2 className="projectTitle">
        {title}
        <small>{tagline}</small>
      </h2>
    );

    const PromoSection = (props) => (
      <div className="section promoSection">
        <div className="promoRow">
          <div className="pluginRowBlock">{props.children}</div>
        </div>
      </div>
    );

    const Button = ({href, target, children}) => (
      <div className="pluginWrapper buttonWrapper">
        <a className="button" href={href} target={target}>
          {children}
        </a>
      </div>
    );

    return (
      <SplashContainer>
        <div className="inner">
          <ProjectTitle
            title="Laminar"
            tagline="Native Scala.js library for building user interfaces"
          />
          <PromoSection>
            <Button href="https://github.com/raquo/Laminar">Github</Button>
            <Button href="https://www.youtube.com/watch?v=L_AHCkl6L-Q">Big Video</Button>
            <Button href={docUrl('examples/hello-world')}>Interactive examples</Button>
            <Button href={docUrl('documentation')}>Documentation</Button>
          </PromoSection>
          <div className="-problem">
            Laminar lets you build web application interfaces, keeping UI state in sync with the underlying application state. Its simple yet expressive patterns build on a rock solid foundation of <a href="https://github.com/raquo/Airstream">Airstream</a> observables and the <a href="https://www.scala-js.org/">Scala.js</a> platform.
          </div>
        </div>
      </SplashContainer>
    );
  }
}

class Index extends React.Component {
  render () {
    const {config: siteConfig, language = ''} = this.props;
    const {baseUrl} = siteConfig;

    return (
      <div>
        <HomeSplash siteConfig={siteConfig} language={language}/>
        <div className="mainHomeContainer">
          {
            this.renderSection({
              title: "Simple",
              layout: "threeColumn",
              contents: [
                {
                  title: 'Approachable',
                  content: 'Easy to understand: no macros, no category theory, no typed effects, few implicits, etc.'
                },
                {
                  title: 'Scalable',
                  content: "Simple abstractions that allow for precise control with no mental overhead, yet scale effortlessly for complex apps."
                },
                {
                  title: 'Native Scala.js',
                  content: 'No dependencies on Javascript libraries or their concepts. No virtual DOM.'
                }
              ]
            })
          }
          {
            this.renderSection({
              title: "Expressive",
              layout: "threeColumn",
              contents: [
                {
                  title: 'Power of Scala',
                  content: 'Use Scala FP and/or OOP techniques instead of having to learn ad-hoc library features.'
                },
                {
                  title: 'Observables',
                  content: 'First-class streams and state variables in one integrated system – [Airstream](https://github.com/raquo/Airstream).'
                },
                {
                  title: 'Diverse Building Blocks',
                  content: 'Everything is abstractable and composable – elements, attributes, attribute-value pairs, events, state, etc.'
                }
              ]
            })
          }
          {
            this.renderSection({
              title: "Safe",
              layout: "threeColumn",
              contents: [
                {
                  title: 'No FRP Glitches',
                  content: 'Exposing inconsistent state is considered normal in most popular streaming libraries. But not in our Airstream.'
                },
                {
                  title: 'Memory Management',
                  content: 'Unique subscription ownership mechanism provides fully automatic memory safety without boilerplate.'
                },
                {
                  title: 'Scala.js > Typescript',
                  content: "Scala's type system is richer, safer, and encourages better patterns than Typescript. [Night and day](https://www.youtube.com/watch?v=kLZr87CGY-U)."
                }
              ]
            })
          }
          {
            this.renderSection({
              title: "Easy Integrations",
              layout: "threeColumn",
              contents: [
                {
                  title: 'Web Components',
                  content: 'Use Material UI or other Web Components just like any other JS dependencies.'
                },
                {
                  title: 'React',
                  content: "Render Laminar elements inside React apps or the other way in just a few lines of code."
                },
                {
                  title: 'Anything Else',
                  content: "Laminar's direct API makes integrations with other JS / DOM / streaming / etc. libraries a breeze."
                }
              ]
            })
          }
          {
            this.renderSection({
              title: "Well Loved",
              layout: "threeColumn",
              contents: [
                {
                  title: '<a target="_blank" href="https://www.reddit.com/r/scala/comments/bi7eka/laminar_v07_featuring_a_new_way_to_performantly/em2aklb/">@teknocide</a>',
                  content: `*"Laminar has been a game changer for me. I never felt comfortable with the available Scala wrappers around React. Enter Laminar, which is lightweight and feels much more natural than what React ever did even when writing it in JavaScript."*`
                },
                {
                  title: '<a target="_blank" href="https://twitter.com/oeiuwq/status/1244844845077798912">@oeiuwq</a>',
                  content: `*"Having used react, cycle, etc, I've to say it's a delight to have Laminar on Scala.js. It just &ast;feels&ast; right, kudos to @raquo for learning and rethinking things instead of blindly following others."*`
                },
                {
                  title: '<a target="_blank" href="https://gitter.im/Laminar_/Lobby?at=5ea1de5d94ac2758e3033436">@kitlangton</a>',
                  content: `*"I've gotta say: Laminar is my favorite way of coding for the web by many miles. It's so ridiculously good!"*`
                }
              ]
            })
          }
          <div className="-homeGetStartedSection x-dark">
            {
              this.renderSection({
                className: "x-dark",
                title: "Get Started",
                layout: "threeColumn",
                preContents: (
                  <React.Fragment>
                    <p>
                      When you have an hour, watch the video introduction below.<br/>
                      It explains both the big ideas and the mechanics behind Laminar.
                    </p>
                    <div className="-homeVideo auto-resizable-iframe">
                      <div>
                        <iframe
                          src="https://www.youtube.com/embed/L_AHCkl6L-Q"
                          frameBorder="0"
                          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowFullScreen
                        />
                      </div>
                    </div>
                    <p>
                      Also, read about how and why Laminar came to be:<br/>
                      <a className="-blogLink" href="https://dev.to/raquo/my-four-year-quest-for-perfect-scala-js-ui-development-b9a">My Four Year Quest For Perfect Scala.js UI Development</a>
                    </p>
                  </React.Fragment>
                ),
                contents: [
                  {
                    title: '[Live Examples](/examples/hello-world)',
                    content: "Live app and its code side-by-side.<br />What sorcery is this?"
                  },
                  {
                    title: '[Documentation](/documentation)',
                    content: 'Always current, explaining both high level ideas and gritty details. Worth reading back-to-back.'
                  },
                  {
                    title: '[Resources](/resources)',
                    content: 'Quickstart, articles & videos, addons, starter kits, and other useful stuff.'
                  },
                ]
              })
            }
            <div className="-homeSection -homeCommunitySection x-dark">
              <h1>Community</h1>
              <p>Our main chatroom is in <a href="https://discord.gg/JTrUxhq7sj">Discord</a>, join us there for news, tips & support!</p>
              <hr />
              <p>Laminar is developed by Nikita Gazarov with generous support from my <a href="https://github.com/sponsors/raquo">sponsors</a>.</p>
              <h2>GOLD Sponsors:</h2>
              <div className="-sponsorsList x-alignItemsEnd">
                <div className="-sponsor x-person x-yurique">
                  <img className="-avatar x-rounded" src="/img/sponsors/yurique.jpg" alt="" />
                  <div className="-text">
                    <div className="-name"><a href="https://github.com/yurique">Iurii Malchenko</a></div>
                  </div>
                </div>
                <div className="-sponsor x-company x-aurinko">
                  <a className="x-noHover" href="https://www.aurinko.io/">
                    <img className="-logo" src="/img/sponsors/aurinko-dark-300px.png" alt="" />
                    <div className="-tagline"><u>Aurinko</u> is an API platform for workplace addons and integrations.</div>
                  </a>
                </div>
                <div className="-sponsor x-company x-heartai">
                  <a className="x-noHover" href="https://www.heartai.net/">
                    <img className="-logo" src="/img/sponsors/heartai-dark.svg" alt="" />
                    <div className="-tagline"><u>HeartAI</u> is a data and analytics platform for digital health and clinical care.</div>
                  </a>
                </div>
              </div>
              <p className="-narrower">Laminar is sponsored by people like you. If you enjoy using Laminar, please consider supporting the project on github. <span className="-emoji">🙏</span></p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  renderSection ({title, preContents, layout, contents, className}) {
    return (
      <Container
        className={"-homeSection " + (className ? className : '')}
      >
        {title && <h1>{title}</h1>}
        {preContents}
        <GridBlock
          align="center"
          contents={contents}
          layout={layout}
        />
      </Container>
    );
  }
}

module.exports = Index;
