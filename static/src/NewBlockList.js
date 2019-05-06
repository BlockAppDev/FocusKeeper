import React, { Component, Fragment } from 'react';
import InputGroup from 'react-bootstrap/InputGroup'
import FormControl from 'react-bootstrap/FormControl'
import { render } from "react-dom";
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
      rows: [item, ...this.state.rows]
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
              <div className="col-md-5 column">
              <h2> Settings </h2>
              <h5> New Block List </ h5>
              <h6> Name </ h6>
              <InputGroup size="sm">
              <FormControl aria-label="Small" aria-describedby="inputGroup-sizing-sm" />
              </InputGroup>
              <br />    
                    
                    <table className="table table-bordered table-hover" id="tab_logic" style = {{width: "100%"}}>
                    <thead>
                      <tr>
                        <th className="text-center"> Applications</th>
                      </tr>
                    </thead>
                    <tbody>
                      <div class="scroll-container">

                      {this.state.rows.map((item, idx) => (
                        <tr id="addr0" key={idx} style = {{width: "100%"}}>
                          <td>
                            <input
                              type="text"
                              name="name"
                              value={this.state.rows[idx].name}
                              onChange={this.handleChange(idx)}
                              style={{"height": "25px", "width": "450px"}}
                              className="form-control" 
                              width= "100%"/>
                            </td>
                          </tr>
                  ))}

                 </div>

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
