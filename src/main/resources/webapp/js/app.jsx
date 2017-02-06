var { Router,
      Route,
      IndexRoute,
      IndexLink,
      Link, browserHistory } = ReactRouter;

function NavBar() {
  return (
    <nav className="navbar navbar-default navbar-fixed-top">
      <div className="container">
        <div className="navbar-header">
          <button type="button" className="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
            <span className="icon-bar"></span>
            <span className="icon-bar"></span>
            <span className="icon-bar"></span>
          </button>
          <Link activeClassName="active" to="/" className="navbar-brand"><img src="/img/logo.jpg" alt="Student Hub" /></Link>
        </div>
        <div className="collapse navbar-collapse" id="myNavbar">
          <ul className="nav navbar-nav navbar-right text-uppercase">
            <li><IndexLink activeClassName="active" to="/">Home</IndexLink></li>
            <li><Link activeClassName="active" to="/my-topics">My Topics</Link></li>
            <li><Link activeClassName="active" to="/users">Users</Link></li>
            <li><Link activeClassName="active" to="/unis">Universities</Link></li>
            <li><Link activeClassName="active" to="/companies">Companies</Link></li>
            <li><Link activeClassName="active" to="/login">Sign In</Link></li>
          </ul>
        </div>
      </div>
    </nav>
  );
}

class App extends React.Component {
  render() {
    return (
      <div>
        <NavBar />
        <div className="content">
          {this.props.children}
        </div>
      </div>
    )
  }
}

ReactDOM.render(
  <Router history={browserHistory}>
    <Route path="/" component={App}>
      <IndexRoute component={Home} />
      <Route path="my-topics" component={MyTopics} />
      <Route path="users" component={UserList} />
      <Route path="unis" component={Universities} />
      <Route path="companies" component={Companies} />
    </Route>
  </Router>,
  document.getElementById('root')
);
