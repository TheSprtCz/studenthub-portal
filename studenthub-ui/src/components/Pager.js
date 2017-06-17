import React, { Component } from 'react';

import _t from '../Translations.js';

class Pager extends Component {
  render() {
    return(
    <div className="col-md-12">
      <nav>
        <ul className="pager">
          { (this.props.currentPage === 0) ? "" :
            <li><a href="#" onClick={() => this.props.pageChanger(-1)}>{ _t.translate("Previous") }</a></li> }
          { (this.props.nextData.lenght === 0 || typeof (this.props.nextData === "undefined") || this.props.nextData === null) ? "" :
            <li><a href="#" onClick={() => this.props.pageChanger(1)}>{ _t.translate("Next") }</a></li> }
        </ul>
      </nav>
    </div>
    );
  }
}

export default Pager;
