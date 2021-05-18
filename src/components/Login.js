import { Link } from "react-router-dom";
import { useState, useEffect } from "react";
import logo from "../cloud.png";

const Login = ({ onLogin, onGoBack }) => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");

    const onSubmit = (e) => {
        e.preventDefault();

        // if (username !== "john") {
        //     alert("You're not john");
        //     return;
        // }

        onLogin(username, password);
    };

    const onBack = () => {
        onGoBack()
    }

    useEffect(() => {
        var elements = document.getElementsByClassName("fa");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.color = "white" : elements.item(i).style.color = "black";
        }

        var elements = document.getElementsByClassName("logo");
        for (var i = 0; i < elements.length; i++) {
            document.body.style.backgroundColor === "black" ? elements.item(i).style.filter="invert(100%)" : elements.item(i).style.filter="invert(0%)";
        }
    })

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <div className="loginContainer">
                <div className="goBack">
                    <Link to="/" onClick={onBack}>Go Back</Link>
                </div>
                <img className="logo" src={logo} alt="Could not load cloud logo." />
                <div className="title unselectable">Login</div>

                <div className="form-control">
                    <label className="unselectable">Username</label>
                    <input
                        type="text"
                        placeholder="Enter your username"
                        // value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                </div>

                <div className="form-control">
                    <label className="unselectable">Password</label>
                    <input
                        type="password"
                        placeholder="Enter your password"
                        // value={username}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                </div>
                <input
                    type="submit"
                    value="Login"
                    className="btn btn-block"
                    readOnly
                />
            </div>
        </form>
    );
};

export default Login;
