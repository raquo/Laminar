/**
 * See https://docusaurus.io/docs/site-config
 */

const siteConfig = {
  title: 'Laminar', // Title for your website.
  tagline: 'Simple, expressive, and safe UI library for Scala.js',
  url: 'https://raquo.github.io',
  baseUrl: '/Laminar/',
  // Used for publishing and more
  projectName: 'Laminar',
  organizationName: 'raquo',
  headerLinks: [
    { doc: 'Documentation', label: 'Docs' },
    { doc: 'examples/Basic', label: 'Examples' },
    { doc: 'Resources', label: 'Resources' }
    // {doc: 'Virtual-DOM', label: 'Virtual DOM'},
    // {
    //   href: 'https://javadoc.io/doc/com.raquo/laminar_sjs1_2.13/latest/com/raquo/laminar/index.html',
    //   label: 'Laminar API',
    //   external: true
    // },
    // {
    //   href: 'https://javadoc.io/doc/com.raquo/airstream_sjs1_2.13/latest/index.html',
    //   label: 'Airstream API',
    //   external: true
    // }
  ],

  // headerIcon: 'img/spiral-logo.svg',
  // footerIcon: 'img/spiral-logo.svg',
  // favicon: 'img/spiral-logo.svg',

  /* Colors for website */
  colors: {
    primaryColor: '#dc322f',
    secondaryColor: '#7f0c1d',
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
  docsUrl: '',
  // Open Graph and Twitter card images.
  ogImage: 'img/spiral-inverse-logo.svg',
  twitterImage: 'img/spiral-inverse-logo.svg',

  customDocsPath: "website/target/mdoc",

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
