/**
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

// See https://docusaurus.io/docs/site-config for all the possible
// site configuration options.

const siteConfig = {
  title: 'Laminar', // Title for your website.
  tagline: 'Simple, expressive, and safe UI library for Scala.js',
  url: 'https://raquo.github.io', // Your website URL
  baseUrl: '/Laminar/', // Base URL for your project */
  // For github.io type URLs, you would set the url and baseUrl like:
  //   url: 'https://facebook.github.io',
  //   baseUrl: '/test-site/',

  // Used for publishing and more
  projectName: 'Laminar',
  organizationName: 'raquo',
  // For top-level user or org sites, the organization is still the same.
  // e.g., for the https://JoelMarcey.github.io site, it would be set like...
  //   organizationName: 'JoelMarcey'

  // For no header links in the top nav bar -> headerLinks: [],
  headerLinks: [
    { doc: 'Documentation', label: 'Docs' },
    { doc: 'Examples', label: 'Examples' },
    // {doc: 'Virtual-DOM', label: 'Virtual DOM'},
    {
      href: 'https://javadoc.io/doc/com.raquo/laminar_sjs1_2.13/latest/com/raquo/laminar/index.html',
      label: 'Laminar API',
      external: true
    },
    {
      href: 'https://javadoc.io/doc/com.raquo/airstream_sjs1_2.13/latest/index.html',
      label: 'Airstream API',
      external: true
    }
  ],


  /* path to images for header/footer */
  headerIcon: 'img/spiral-logo.svg',
  footerIcon: 'img/spiral-logo.svg',
  favicon: 'img/spiral-logo.svg',

  /* Colors for website */
  colors: {
    primaryColor: '#30887e',
    secondaryColor: '#88303A',
  },

  /* Custom fonts for website */
  /*
  fonts: {
    myFont: [
      "Times New Roman",
      "Serif"
    ],
    myOtherFont: [
      "-apple-system",
      "system-ui"
    ]
  },
  */

  // This copyright info is used in /core/Footer.js and blog RSS/Atom feeds.
  copyright: `Copyright © ${new Date().getFullYear()} Nikita Gazarov`,

  highlight: {
    // Highlight.js theme to use for syntax highlighting in code blocks.
    theme: 'github',
  },

  // Add custom scripts here that would be placed in <script> tags.
  scripts: ['https://buttons.github.io/buttons.js'],

  // On page navigation for the current documentation page.
  onPageNav: 'separate',
  // No .html extensions for paths.
  cleanUrl: true,

  // Open Graph and Twitter card images.
  ogImage: 'img/spiral-inverse-logo.svg',
  twitterImage: 'img/spiral-inverse-logo.svg',
  customDocsPath: "laminar-docs/target/mdoc",


  // For sites with a sizable amount of content, set collapsible to true.
  // Expand/collapse the links and subcategories under categories.
  // docsSideNavCollapsible: true,

  // Show documentation's last contributor's name.
  // enableUpdateBy: true,

  // Show documentation's last update time.
  // enableUpdateTime: true,

  // You may provide arbitrary config keys to be used as needed by your
  // template. For example, if you need your repo's URL...
  repoUrl: 'https://github.com/raquo/Laminar',
};

module.exports = siteConfig;
