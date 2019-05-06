import React, { Component } from 'react';
import { Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap';

class MainMenu extends Component {
	constructor(props) {
    	super(props);

    	this.toggle = this.toggle.bind(this);
	    this.onMouseEnter = this.onMouseEnter.bind(this);
	    this.onMouseLeave = this.onMouseLeave.bind(this);
	    this.state = {
	      dropdownOpen: false
	    };
  	}

  	toggle() {
	    this.setState(prevState => ({
	      dropdownOpen: !prevState.dropdownOpen
	    }));
	  }

	  onMouseEnter() {
	    this.setState({dropdownOpen: true});
	  }

	  onMouseLeave() {
	    this.setState({dropdownOpen: false});
	  }

	  handleOnClick = () => {
		console.log(this.props);
		const { history } = this.props;
		history.push('/NewBlockList');
	}

  	render() {
  		return (
  			<div className="MainMenu" class = "text-center">
				<Dropdown className="d-inline-block" onMouseOver={this.onMouseEnter} onMouseLeave={this.onMouseLeave} isOpen={this.state.dropdownOpen} toggle={this.toggle}>
			        <h1>Main Menu</h1>
			        <DropdownToggle caret> Modify Settings </DropdownToggle>
			        <DropdownMenu> <DropdownItem onClick={this.handleOnClick} type="button"> Manage Block Lists</DropdownItem>
			        <DropdownItem divider />
			        <DropdownItem> Schedule Block Session </DropdownItem>
			        </DropdownMenu>
			        <button class = "muted-button" onClick={this.handleOnClick} type="button">View Usage History</button>
	       			<button class = "muted-button" onClick={this.handleOnClick} type="button">Start Block Session</button>
		      	</Dropdown>
		      	
	       </div>
	    );
  	}
}

export default MainMenu;
