import React from 'react';
import TopicTable from '../components/TopicTable.js'

// title page
class HomeView extends React.Component {
  render() {
    return (
      <div>
        <h2>Topics</h2>
        <TopicTable />
      </div>
    )
  }
}

export default HomeView
