import React from 'react';

import SiteSnackbar from '../components/SiteSnackbar.js';

import _t from '../Translations.js';

class ResetConfirmation extends React.Component {
  state = {snackbarActive: false, snackbarLabel: ''};

  componentDidMount() {
    this.handleCheck();
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  }

  handleCheck = () => {
    fetch('/api/account/confirmReset?secret=' + this.props.secret +
      '&id='+this.props.id, {
      method: 'post'
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          snackbarLabel: "Your password has been succesfully reset!",
          snackbarActive: true
        });
      } else {
        this.setState({
          snackbarLabel: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding.",
          snackbarActive: true
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  }

  handleToggle = () => {
    this.setState({snackbarActive: !this.state.snackbarActive});
  }

  render() {
    return (
      <div>
        <SiteSnackbar active={this.state.snackbarActive} label={this.state.snackbarLabel}
          toggleHandler={() => this.handleToggle()} />
      </div>
    )
  }
}

const ResetConfirmationView = ({ match }) => (
  <div className='text-center col-md-offset-3 col-md-6'>
    <h1>{ _t.translate('Password reset confirmation') }</h1>
    <ResetConfirmation id={match.params.id} secret={match.params.secret} />
  </div>
);

export default ResetConfirmationView
