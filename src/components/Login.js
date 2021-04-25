import { Link } from "react-router-dom";
import { useState } from "react";
import logo from "../cloud.png";

const Login = ({ onLogin }) => {
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

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <div className="loginContainer">
                <div className="goBack">
                    <Link to="/">Go Back</Link>
                </div>
                <img src={logo} alt="Could not load cloud logo." />
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
                        type="text"
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
