import React from 'react';
import Dropdown from 'react-toolbox/lib/dropdown/Dropdown.js';

import Util from '../Util.js';
import _t from '../Translations.js';

class CountrySelect extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      value: (Util.isEmpty(this.props.currentCountry)) ? 0: this.props.currentCountry.tag,
      labels: []
    };

    this.getCountryLabels();
  }

  /**
   * Sets default input values on props change.
   */
  componentWillReceiveProps(nextProps) {
    if (this.props === nextProps) return;

    this.setState({
      value: (Util.isEmpty(nextProps.currentCountry)) ? 0 : nextProps.currentCountry.tag
    });
  }

  handleChange = (value) => {
    this.setState({value: value});
    this.props.changeHandler({tag: value});
  };

  getCountryLabels() {
    fetch('/api/countries', {
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
          value: json[i].tag,
          label: json[i].name
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
        label={ _t.translate('Country') }
        onChange={this.handleChange}
        source={this.state.labels}
        value={this.state.value}
        icon='public' />
    );
  }
}

export default CountrySelect;
