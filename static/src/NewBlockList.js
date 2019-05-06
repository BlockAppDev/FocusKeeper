import React, { Component, Fragment } from 'react';
import InputGroup from 'react-bootstrap/InputGroup'
import FormControl from 'react-bootstrap/FormControl'
import Form from 'react-bootstrap/Form'


class NewBlockList extends Component {
	state = {
    rows: []
  };
 
  handleChange = idx => e => {
    const { name, value } = e.target;
    const rows = [...this.state.rows];
    rows[idx] = {
      [name]: value
    };
    this.setState({
      rows
    });
  };
  
  handleAddRow = () => {
    const item = {
      name: ""
    };
    this.setState({
      rows: [...this.state.rows, item]
    });
  };
 
  handleRemoveRow = () => {
    this.setState({
      rows: this.state.rows.slice(0, -1)
    });
  };
  
  render() {
    return (
    	<div className = "NewBlockList">
    		<div className="container">
        		<div className="row clearfix">
        			<div className="col-md-4 column">
    					<h2> Settings </h2>
    					<h5> New Block List </ h5>
    					<h6> Name </ h6>
			  			<InputGroup size="sm" className="mb-3">
			  			<FormControl aria-label="Small" aria-describedby="inputGroup-sizing-sm" />
			  			</InputGroup>
			  			<br /> 	  
              			
              			<table className="table table-bordered table-hover" id="tab_logic">
                		<thead>
		                  <tr>
		                    <th className="text-center"> Applications</th>
		                  </tr>
		                </thead>
		                <tbody>

		                  {this.state.rows.map((item, idx) => (
		                    <tr id="addr0" key={idx}>
		                      <td>
		                        <input
		                          type="text"
		                          name="name"
		                          value={this.state.rows[idx].name}
		                          onChange={this.handleChange(idx)}
		                          style={{"height": "20px"}}
		                          className="form-control" />
                      		  </td>
                    	    </tr>
                  ))}
                </tbody>
              </table>
              <img src = "plus.svg" onClick={this.handleAddRow} className="btn btn-default pull-left"/>
              <img src = "minus.svg" onClick={this.handleRemoveRow} className="pull-right btn btn-default"/>
            </div>
          </div>
        </div>
      </div>
				
    );
  }
}

export default NewBlockList;
