import React, { Component } from 'react';
import Table from 'react-toolbox/lib/table/Table.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import EditButton from './EditButton.js';
import DeleteButton from './DeleteButton.js';
import UserEditDialog from './UserEditDialog.js';

import Util from '../Util.js';
import _t from '../Translations.js';

/**
 * Renders the users vies for Admins.
 * @param users                                  list of all users
 * @param dataHandler(method, id, data)     defines the function to call on Button clicks
 */
class AdminUsersView extends Component {
  state = { dialogActive: false, editId: -1 }

  /**
   * Toggle the visiblity of the edit Dialog.
   */
  toggleDialog = (id) => {
    this.setState({
      dialogActive: !this.state.dialogActive,
      editId: id
    });
  }

  render() {
    return(
      <div>
        <Table multiSelectable={false} selectable={false}>
          <TableHead>
            <TableCell numeric>ID</TableCell>
            <TableCell>{ _t.translate('Username') }</TableCell>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>{ _t.translate('Phone') }</TableCell>
            <TableCell>{ _t.translate('Faculty') }</TableCell>
            <TableCell>{ _t.translate('Company') }</TableCell>
            <TableCell>{ _t.translate('Role') }</TableCell>
            <TableCell>{ _t.translate('Last login') }</TableCell>
            <TableCell>{ _t.translate('Tags') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.props.users.map( (user, index) => (
            <TableRow key={index}>
              <TableCell numeric>
                {user.id}
              </TableCell>
              <TableCell>
                {user.username}
              </TableCell>
              <TableCell>
                {user.name}
              </TableCell>
              <TableCell>
                {user.email}
              </TableCell>
              <TableCell>
                {user.phone}
              </TableCell>
              <TableCell>
                {(Util.isEmpty(user.faculty)) ? "None" : user.faculty.name}
              </TableCell>
              <TableCell>
                {(Util.isEmpty(user.company)) ? "None" : user.company.name}
              </TableCell>
              <TableCell>
                { user.roles.map( (role, index) => <Chip key={index}> {role} </Chip> ) }
              </TableCell>
              <TableCell>
                {(Util.isEmpty(user.lastLogin)) ? "Haven't yet logged in" : new Date(user.lastLogin).toString()}
              </TableCell>
              <TableCell>
                { user.tags.map( (tag, index) => <Chip key={index}> {tag} </Chip> ) }
              </TableCell>
              <TableCell>
                <span className="pull-left">
                  <EditButton toggleHandler={() => this.toggleDialog(index)} />
                </span>
                <span>
                  <DeleteButton deleteHandler={() => this.props.dataHandler("delete", user.id, null)} />
                </span>
              </TableCell>
            </TableRow>
          ))}
        </Table>
        <UserEditDialog
          active={this.state.dialogActive}
          user={(this.state.editId === -1) ? -1 : this.props.users[this.state.editId]}
          editHandler={(user) => this.props.dataHandler("put", (this.state.editId === -1) ? -1 : this.props.users[this.state.editId].id, user)}
          toggleHandler={() => this.toggleDialog(-1)}
        />
    </div>
    );
  }
}

export default AdminUsersView;
