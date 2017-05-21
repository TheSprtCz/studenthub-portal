import React, { Component } from 'react';
import Button from 'react-toolbox/lib/button/Button.js';
import Dialog from 'react-toolbox/lib/dialog/Dialog.js';
import Input from 'react-toolbox/lib/input/Input.js';

/**
 * Renders a bug reporting button.
 */
class ContactAdminButton extends Component {
  constructor(props) {
    super(props);

    this.state = {
      active: false,
      message: "",
      subject: ""
    };
  }

  actions = [
    { label: "Send", onClick: () => this.handleSend()},
    { label: "Cancel", onClick: () => this.handleToggle() }
  ];

  handleSend = () => {
    window.location.href = "mailto:email@domain.cz?subject="+this.state.subject+"&amp;body="+this.state.message;
    this.setState({ active: false });
  };

  handleChange = (name, value) => {
    this.setState({...this.state, [name]: value});
  };

  handleToggle = () => {
    this.setState({ active: !this.state.active });
  };

  render() {
    return(
      <span>
        <Button label="Contact us" onClick={() => this.handleToggle()} icon="feedback" />
        <Dialog
          actions={this.actions}
          active={this.state.active}
          onEscKeyDown={this.handleToggle}
          onOverlayClick={this.handleToggle}
          title="Send us a message">
          <p>Here you can tell us your thoughts</p>
          <Input type='text' label='Subject' hint="Your message subject" name='subject' required value={this.state.subject} onChange={this.handleChange.bind(this, 'subject')} />
          <Input type='text' label='Message' hint="Body of your message" name='message' multiline rows={10} required value={this.state.message} onChange={this.handleChange.bind(this, 'message')} />
        </Dialog>
      </span>
    );
  }
}

export default ContactAdminButton;
