import React, { Component } from 'react';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import { tableFactory } from 'react-toolbox/lib/table/Table.js';

import EditButton from './EditButton.js';
import DeleteButton from './DeleteButton.js';

class FacultyRow extends Component {
  render () {
    return (
      <TableRow>
        <TableCell numeric>
          {this.props.fac.id}
        </TableCell>
        <TableCell>
          {this.props.fac.name}
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

class FacultyHead extends Component {
  render () {
    return (
      <TableHead>
        <TableCell numeric key="id">
          ID
        </TableCell>
        <TableCell key="name">
          Name
        </TableCell>
        <TableCell key="actions">
          Actions
        </TableCell>
      </TableHead>
    );
  }
}

const RTTable = tableFactory(FacultyHead, FacultyRow)

class FacultyTable extends Component {
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

export { FacultyTable, FacultyRow, FacultyHead }
