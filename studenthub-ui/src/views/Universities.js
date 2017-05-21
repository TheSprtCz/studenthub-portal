import React, { Component } from 'react';
import Layout from 'react-toolbox/lib/layout/Layout.js';
import Panel from 'react-toolbox/lib/layout/Panel.js';

import ContentTable from '../components/ContentTable.js';

class Universities extends Component {
  render() {
    return (
      <Layout>
        <Panel className="App-panel">
          <ContentTable />
        </Panel>
      </Layout>
    );
  }
}

export default Universities;
