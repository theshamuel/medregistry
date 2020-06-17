import React from 'react';

class LoginForm extends React.PureComponent {

    constructor(props) {
        super(props);
        this.state = {login: "", password: "", formError: "", isLoading: false}
    }

    handleField = (event) => {
        event.preventDefault()
        this.setState({
            [event.target.name]: event.target.value,
            formError:"",
        })
    }

    handleForm = (event) => {
        event.preventDefault()
        console.log("handleForm->", this.state)
        this.setState({isLoading: true})
        fetch("http://localhost:9000/auth/", {
            method: "post",
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                login: this.state.login,
                password: this.state.password
            })
        }).then((response) => {
            return response.json()
        }).then(data => {
            //TODO:fix different status
            if (data.code !== 200) {
                this.setState({formError: data.message})
            }
            this.setState({login: "", password: "", isLoading: false})
        }).catch((response) => {
            this.setState({login: "", password: "", isLoading: false})
        });
    }

    //TODO: fix twice render
    render() {
        return (
            <div>
                <h1>{this.props.title}</h1>
                <form onSubmit={this.handleForm}>
                    <div>
                        <label>Логин</label>
                        <input name="login" type="text" autoComplete="off" placeholder="Введите логин..."
                               required onChange={this.handleField} value={this.state.login}/>
                    </div>
                    <div>
                        <label>Пароль</label>
                        <input name="password" type="password" placeholder="Введите пароль..." required
                               onChange={this.handleField} value={this.state.password}/>
                    </div>
                    <div>
                        <button disabled={this.state.login === "" || this.state.password === "" || this.state.isLoading ? true : null}>Войти
                        </button>
                        {this.state.isLoading && <img src="data:image/gif;base64,R0lGODlhEAAQAPIAAP///wAAAMLCwkJCQgAAAGJiYoKCgpKSkiH/C05FVFNDQVBFMi4wAwEAAAAh/hpDcmVhdGVkIHdpdGggYWpheGxvYWQuaW5mbwAh+QQJCgAAACwAAAAAEAAQAAADMwi63P4wyklrE2MIOggZnAdOmGYJRbExwroUmcG2LmDEwnHQLVsYOd2mBzkYDAdKa+dIAAAh+QQJCgAAACwAAAAAEAAQAAADNAi63P5OjCEgG4QMu7DmikRxQlFUYDEZIGBMRVsaqHwctXXf7WEYB4Ag1xjihkMZsiUkKhIAIfkECQoAAAAsAAAAABAAEAAAAzYIujIjK8pByJDMlFYvBoVjHA70GU7xSUJhmKtwHPAKzLO9HMaoKwJZ7Rf8AYPDDzKpZBqfvwQAIfkECQoAAAAsAAAAABAAEAAAAzMIumIlK8oyhpHsnFZfhYumCYUhDAQxRIdhHBGqRoKw0R8DYlJd8z0fMDgsGo/IpHI5TAAAIfkECQoAAAAsAAAAABAAEAAAAzIIunInK0rnZBTwGPNMgQwmdsNgXGJUlIWEuR5oWUIpz8pAEAMe6TwfwyYsGo/IpFKSAAAh+QQJCgAAACwAAAAAEAAQAAADMwi6IMKQORfjdOe82p4wGccc4CEuQradylesojEMBgsUc2G7sDX3lQGBMLAJibufbSlKAAAh+QQJCgAAACwAAAAAEAAQAAADMgi63P7wCRHZnFVdmgHu2nFwlWCI3WGc3TSWhUFGxTAUkGCbtgENBMJAEJsxgMLWzpEAACH5BAkKAAAALAAAAAAQABAAAAMyCLrc/jDKSatlQtScKdceCAjDII7HcQ4EMTCpyrCuUBjCYRgHVtqlAiB1YhiCnlsRkAAAOwAAAAAAAAAAAA=="/>}
                    </div>
                    {!!this.state.formError && <div>{this.state.formError}</div>}
                </form>
            </div>
        );
    }
}


export default LoginForm;
