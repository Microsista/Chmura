import { Link } from "react-router-dom";
import { useState } from "react";
import logo from "../cloud.png";
// import dp from "react-datepicker";
import DatePicker from "react-datepicker";

const SignUp = ({ onSignUp }) => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [dob, setDob] = useState(new Date());

    const onSubmit = (e) => {
        e.preventDefault();

        // if (username !== "john") {
        //     alert("You're not john");
        //     return;
        // }

        onSignUp(username, email, password, dob);
    };

    return (
        <form className="login-form" onSubmit={onSubmit}>
            <div className="loginContainer">
                <div className="goBack">
                    <Link to="/">Go Back</Link>
                </div>
                <img src={logo} alt="Could not load cloud logo." />
                <div className="title unselectable">Sign up</div>

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
                    <label className="unselectable">Email</label>
                    <input
                        type="text"
                        placeholder="Enter your email"
                        // value={username}
                        onChange={(e) => setEmail(e.target.value)}
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
                <div>
                    <label className="unselectable">Date of Birth</label>
                    <div>
                        <DatePicker
                            selected={dob}
                            onChange={(date) => setDob(date)}
                        />
                    </div>
                </div>

                <input
                    type="submit"
                    value="Sign up"
                    className="btn btn-block"
                    readOnly
                />
            </div>
        </form>
    );
};

export default SignUp;
