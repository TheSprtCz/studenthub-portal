import React, { Component } from 'react';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import { tableFactory } from 'react-toolbox/lib/table/Table.js';

import EditButton from './EditButton.js';
import DeleteButton from './DeleteButton.js';

class UniversityRow extends Component {
  render () {
    return (
      <TableRow>
        <TableCell numeric>
          {this.props.uni.id}
        </TableCell>
        <TableCell>
          {this.props.uni.logoUrl}
        </TableCell>
        <TableCell onClick={() => this.props.clickHandler()}>
          <span className="CellLink">
            {this.props.uni.name}
          </span>
        </TableCell>
        <TableCell>
          {this.props.uni.city}
        </TableCell>
        <TableCell>
          {this.props.uni.country}
        </TableCell>
        <TableCell>
          {this.props.uni.url}
        </TableCell>
        <TableCell>
          <table>
            <tbody>
              <tr>
                <td>
                  <EditButton toggleHandler={() => this.props.toggleHandler()} />
                </td>
                <td>
                  <DeleteButton deleteHandler={() => this.props.deleteHandler()} />
                </td>
              </tr>
            </tbody>
          </table>
        </TableCell>
      </TableRow>
    )
  }
}

class UniversityHead extends Component {
  render () {
    return (
      <TableHead>
        <TableCell numeric key="id">
          ID
        </TableCell>
        <TableCell key="logo">
          Logo
        </TableCell>
        <TableCell key="name">
          Name
        </TableCell>
        <TableCell key="city">
          City
        </TableCell>
        <TableCell key="state">
          Country
        </TableCell>
        <TableCell key="site"
          >Website
        </TableCell>
        <TableCell key="actions">
          Actions
        </TableCell>
      </TableHead>
    )
  }
}

const RTTable = tableFactory(UniversityHead, UniversityRow)

class UniversityTable extends Component {
  render () {
    const { children, ...props} = this.props

    return (
      <div>
        <RTTable {...props}>
          {children}
        </RTTable>
      </div>
    )
  }
}

export { UniversityTable, UniversityRow, UniversityHead }
