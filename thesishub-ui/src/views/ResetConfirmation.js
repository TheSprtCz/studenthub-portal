import React from 'react';

import _t from '../Translations.js';

class ResetConfirmation extends React.Component {

  componentDidMount() {
    this.handleCheck();
  }

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  }

  handleCheck = () => {
    fetch('/api/account/confirmReset?secret=' + this.props.secret + '&id='+this.props.id, {
      method: 'post'
    }).then(function(response) {
      if (response.ok) {
        this.setState({
          label: "Your password has been succesfully reset!"
        });
      } else {
        this.setState({
          label: "An error occured! Your request couldn't be processed. It's possible that you have a problem with your internet connection or that the server is not responding."
        });
        throw new Error('There was a problem with network connection. POST request could not be processed!');
      }
    }.bind(this));
  }

  render() {
    return (
      <p>{ this.state.label }</p>
    )
  }
}

const ResetConfirmationView = ({ location }) => (
  <div className='text-center col-md-offset-3 col-md-6'>
    <h1>{ _t.translate('Password reset confirmation') }</h1>
    <ResetConfirmation id={ new URLSearchParams(location.search).get('id') } secret={ new URLSearchParams(location.search).get('secret') } />
  </div>
);

export default ResetConfirmationView
