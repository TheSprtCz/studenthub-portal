var { Router,
      Route,
      IndexRoute,
      IndexLink,
      Link, browserHistory } = ReactRouter;

var App = React.createClass({
  render: function() {
    return (
      <div>
        <h1>Student Hub</h1>
        <ul className="header">
          <li><IndexLink activeClassName="active" to="/">Home</IndexLink></li>
          {/*
          <li><Link activeClassName="active" to="/stuff">Stuff</Link></li>
          <li><Link activeClassName="active" to="/contact">Contact</Link></li>
          */}
        </ul>
        <div className="content">
          {this.props.children}
        </div>
      </div>
    )
  }
});

ReactDOM.render(
  <Router history={browserHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={Home} />
      {/*
      <Route path="stuff" component={Stuff} />
      <Route path="contact" component={Contact} />
      */}
    </Route>
  </Router>,
  document.getElementById('root')
);
