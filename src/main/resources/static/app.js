'use strict';

const React = require('react');
const client = require('./client');
const EmployeeList = require('./employee/EmployeeList.jsx');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {employees: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/api/employees'}).done(response => {
            this.setState({employees: response.entity._embedded.employees});
        });
    }

    render() {
        return (
            <EmployeeList employees={this.state.employees}/>
        )
    }
}

React.render(
    <App />,
    document.getElementById('react')
);
