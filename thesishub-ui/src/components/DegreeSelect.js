import React, { Component } from 'react';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';

import Util from '../Util.js';
import _t from '../Translations.js';

/**
 * Dropdown containing Topic Degrees
 */
class DegreeSelect extends Component {
  constructor(props) {
    super(props);

    this.state = {
      value: (Util.isEmpty(this.props.currentDegree)) ? '': this.props.currentDegree.name,
      labels: []
    };

    this.getDegreeLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.currentDegree)) ? '' : nextProps.currentDegree.name
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({name: value});
  };

  getDegreeLabels() {
    fetch('/api/degrees', {
      credentials: 'same-origin',
      method: 'get'
    }).then(function(response) {
      if (response.ok) {
        return response.json();
      } else {
        throw new Error('There was a problem with network connection.');
      }
    }).then(function(json) {
      var newData = [];

      for(let i in json) {
        newData.push({
          value: json[i].name,
          label: json[i].description
        });
      }
      this.setState({
        labels: newData
      });
    }.bind(this));
  }

  render () {
    return (
      <Dropdown
        auto
        label={ _t.translate('Degree') }
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='account_balance' />
    );
  }
}

export default DegreeSelect;
