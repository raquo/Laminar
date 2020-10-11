const React = require('react');

class Footer extends React.Component {
  docUrl (doc) {
    const baseUrl = this.props.config.baseUrl;
    const docsUrl = this.props.config.docsUrl;
    const docsPart = `${docsUrl ? `${docsUrl}/` : ''}`;
    return `${baseUrl}${docsPart}${doc}`;
  }



  render () {
    return (
      <footer className="nav-footer" id="footer">
        <section className="sitemap">
          <a href={this.props.config.baseUrl} className="nav-home">
            {this.props.config.footerIcon && (
              <img
                src={this.props.config.baseUrl + this.props.config.footerIcon}
                alt={this.props.config.title}
                width="66"
                height="58"
              />
            )}
          </a>
          <div className="community-column">
            <h5>Community</h5>
            <a href="https://gitter.im/Laminar_/Lobby">Gitter chatroom</a>
            <a href="https://github.com/sponsors/raquo">Sponsor the project</a>
          </div>
          <div className="github-column">
            <h5>Github</h5>
            <p>
              <a
                className="github-button"
                data-icon="octicon"
                href={this.props.config.laminarRepoUrl}
              >
                Laminar
              </a>
            </p>
            <p>
              <a
                className="github-button"
                data-icon="octicon"
                href={this.props.config.airstreamRepoUrl}
              >
                Airstream
              </a>
            </p>
            <p>
              <a
                className="github-button"
                href={this.props.config.laminarRepoUrl}
                data-icon="octicon-star"
                data-count-href="/raquo/Laminar/stargazers"
                data-show-count="true"
                data-count-aria-label="# stargazers on GitHub"
                aria-label="Star this project on GitHub"
              >
                Star
              </a>
            </p>
          </div>
        </section>
        <section className="copyright">{this.props.config.copyright}</section>
        <script dangerouslySetInnerHTML={{__html: `
          <!-- Basic Woopra analytics -->
          (function(){
            var t,i,e,n=window,o=document,a=arguments,s="script",r=["config","track","identify","visit","push","call","trackForm","trackClick"],c=function(){var t,i=this;for(i._e=[],t=0;r.length>t;t++)(function(t){i[t]=function(){return i._e.push([t].concat(Array.prototype.slice.call(arguments,0))),i}})(r[t])};for(n._w=n._w||{},t=0;a.length>t;t++)n._w[a[t]]=n[a[t]]=n[a[t]]||new c;i=o.createElement(s),i.async=1,i.src="//static.woopra.com/js/w.js",e=o.getElementsByTagName(s)[0],e.parentNode.insertBefore(i,e)
          })("woopra");
          woopra.config({
            domain: 'laminar.dev'
          });
          woopra.track();
          
        `}} />
      </footer>
    );
  }
}

module.exports = Footer;
