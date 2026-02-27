import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
  plugins: [scalaJSPlugin({
    cwd: "..",        // sbt root is parent directory
    projectID: "websiteJS"
  })],
  build: {
    outDir: "dist",
    lib: {
      entry: "src/main/js/main.js",
      formats: ["iife"],
      name: "WebsiteJS",
      fileName: () => "websitejs-library.js"
    },
    rollupOptions: {
      output: {
        inlineDynamicImports: true
      }
    }
  }
});
