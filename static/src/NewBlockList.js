import React, { Component} from 'react';
import InputGroup from 'react-bootstrap/InputGroup'
import FormControl from 'react-bootstrap/FormControl'

class NewBlockList extends Component {
  state = { AppRows: [] };
  state2 = { WebRows: [] };
 
  handleChange = idx => e => {
    const { name, value } = e.target;
    const AppRows = [...this.state.AppRows];
    AppRows[idx] = {[name]: value};
    this.setState({AppRows});
  };

  handleChange2 = idx => e => {
    const { name, value } = e.target;
    const WebRows = [...this.state2.WebRows];
    WebRows[idx] = { [name]: value};
    this.setState({WebRows});
  };
  
  handleAddRow = () => {
    const item = { name: ""};
    this.setState({AppRows: [item, ...this.state.AppRows]});
  };

  handleAddRow2 = () => {
    const item = {name: ""};
    this.setState({WebRows: [item, ...this.state2.WebRows]});
  };
 
  handleRemoveRow = () => {
    this.setState({AppRows: this.state.AppRows.slice(0, -1)});
  };

  handleRemoveRow2 = () => {
    this.setState({WebRows: this.state2.WebRows.slice(0, -1)});
  };
  
  render() {
    return (
      <div className = "NewBlockList">
        <div className="container">
          <div className="row clearfix">
            <div className="col-sm-10 col-form-label col-form-label-sm">
              <font size="5"> Settings </font> <br/> 
              <font size="3"> New Block List </font> <br/> 
              <font size="2"> Name </font> <br/> 
                <InputGroup size="input-xs" style= {{width: "350px"}}>
                  <FormControl aria-label="Small" aria-describedby="input-xs" />
                </InputGroup>
                <br />    
                    
                <table className="table table-bordered table-hover"  id="tab_logic" style = {{height: "20px", width: "350px"}}>
                  <thead>
                    <tr>
                      <th className="text-center"> Applications</th>
                    </tr>
                  </thead>
                  <tbody>
                    <div class="scroll-container">
                      {this.state.AppRows.map((item, idx) => (
                        <tr id="addr0" key={idx} style = {{height: "20px", width: "350px"}}>
                          <td>
                            <input
                              type="text"
                              name="name"
                              value={this.state.AppRows[idx].name}
                              onChange={this.handleChange(idx)}
                              style={{"height": "20px", "width": "350px"}}
                              className="form-control"/>
                          </td>
                        </tr>
                      ))}
                    </div>

                  </tbody>
                </table>
              <img src = "plus.svg" alt="+" onClick={this.handleAddRow} className="btn btn-default pull-left"/>
              <img src = "minus.svg" alt="-" onClick={this.handleRemoveRow} className="pull-right btn btn-default"/>
              
            </div>
          </div>
        </div>

        <div className="container">
          <div className="row clearfix">
            <div className="col-sm-10 col-form-label col-form-label-sm">
              <table className="table table-bordered table-hover"  id="tab_logic" style = {{height: "20px", width: "350px"}}>
                <thead>
                  <tr>
                    <th className="text-center"> Websites</th>
                  </tr>
                </thead>
                <tbody>
                  <div class="scroll-container">
                    {this.state2.WebRows.map((item, idx) => (
                      <tr id="addr0" key={idx} style = {{height: "20px", width: "350px"}}>
                        <td>
                          <input
                            type="text"
                            name="name"
                            value={this.state2.WebRows[idx].name}
                            onChange={this.handleChange2(idx)}
                            style={{"height": "20px", "width": "350px"}}
                            className="form-control"/>
                        </td>
                      </tr>
                    ))}
                  </div>
                </tbody>
              </table>
              <img src = "plus.svg" alt="+" onClick={this.handleAddRow2} className="btn btn-default pull-left"/>
              <img src = "minus.svg" alt="-" onClick={this.handleRemoveRow2} className="pull-right btn btn-default"/>
            </div>
          </div>
        </div>
      </div>      
    );
  }
}

export default NewBlockList;