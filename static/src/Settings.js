import React, { Component, Fragment } from 'react';

class Settings extends Component {

  handleOnClick = () => {
    console.log(this.props);
    const { history } = this.props;
    history.push('/NewBlockList');
  }
    
  
  render() {
    return (
      <div className = "Settings">
        <div className="container">
            <div className="row clearfix">
              <div className="col-md-4 column">
                <h2> Settings </h2>
                <h5> Block Lists</ h5>
                <div onClick={this.handleOnClick} value="text" className="text_color"> + Create New List
              </div>
            </div>
          </div>
        </div>
      </div>

    );
  }
}




export default Settings;
