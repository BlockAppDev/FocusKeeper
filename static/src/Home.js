import React, { Component } from 'react';


class Home extends Component {

	handleOnClick = () => {
		console.log(this.props);
		const { history } = this.props;
		history.push('/menu');
	}
  render() {
    return (
      <div className="Home" class = "text-center">

        <h1> Welcome to FocusKeeper! </ h1>

     
       <button class = "muted-button" onClick={this.handleOnClick} type="button">Let's Get Started!</button>


      </ div>
    )
  }
}

export default Home;