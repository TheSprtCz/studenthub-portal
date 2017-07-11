import React, { Component } from 'react';
import ReactPaginate from 'react-paginate';
import './pager.css';

import _t from '../Translations.js';

class Pager extends Component {
  render() {
    return(
      <div className="paginate text-center">
        <ReactPaginate previousLabel="<"
                       nextLabel=">"
                       breakLabel="…"
                       pageCount={this.props.pages}
                       marginPagesDisplayed={2}
                       pageRangeDisplayed={5}
                       onPageChange={this.props.pageChanger}
                       previousLinkClassName="btn btn-link btn-lg"
                       nextLinkClassName="btn btn-link btn-lg"
                       pageLinkClassName="btn btn-link btn-lg"
                       activeClassName="apage"
                       disabledClassName="transparent" />
      </div>
    );
  }
}

export default Pager;
