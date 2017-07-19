import React, { Component } from 'react';
import Avatar from 'react-toolbox/lib/avatar/Avatar.js';
import Table from 'react-toolbox/lib/table/Table.js';
import TableCell from 'react-toolbox/lib/table/TableCell.js';
import TableHead from 'react-toolbox/lib/table/TableHead.js';
import TableRow from 'react-toolbox/lib/table/TableRow.js';
import Chip from 'react-toolbox/lib/chip/Chip.js';

import EditButton from './EditButton.js';
import DeleteButton from './DeleteButton.js';
import RestrictedUserEditDialog from './RestrictedUserEditDialog.js';

import Util from '../Util.js';
import _t from '../Translations.js';

const gravatar = require("gravatar")

class AmbassadorUsersView extends Component {
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
            <TableCell>Gravatar</TableCell>
            <TableCell>{ _t.translate('Name') }</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>{ _t.translate('Phone') }</TableCell>
            <TableCell>{ _t.translate('Last login') }</TableCell>
            <TableCell>{ _t.translate('Tags') }</TableCell>
            <TableCell>{ _t.translate('Actions') }</TableCell>
          </TableHead>
          {this.props.users.map( (user, index) => (
            <TableRow key={index}>
              <TableCell>
                <Avatar image={gravatar.url(user.email, {protocol: 'https'})} title="User Gravatar" />
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
        <RestrictedUserEditDialog
          active={this.state.dialogActive}
          user={(this.state.editId === -1) ? -1 : this.props.users[this.state.editId]}
          editHandler={(user) => this.props.dataHandler("put",
            (this.state.editId === -1) ? -1 : this.props.users[this.state.editId].id, user)}
          toggleHandler={() => this.toggleDialog(-1)}
        />
      </div>
    );
  }
}

export default AmbassadorUsersView;
