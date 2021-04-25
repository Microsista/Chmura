import { useState } from "react";

const AddFile = ({ onAddFile, onAdd }) => {
    const [name, setName] = useState("");
    const [cont, setCont] = useState("");

    const onSubmit = (e) => {
        e.preventDefault();

        if (!name) {
            alert("Please add a name");
            return;
        }

        onAddFile({ name, cont });

        setName("");
        setCont("");
        onAdd();
    };

    return (
        <form className="add-form" onSubmit={onSubmit}>
            <div className="form-control">
                <label className="unselectable">Name</label>
                <input
                    type="text"
                    placeholder="Enter filename"
                    // value={username}
                    onChange={(e) => setName(e.target.value)}
                />
            </div>

            <div className="form-control">
                <label className="unselectable">Content</label>
                <input
                    type="text"
                    placeholder="Enter file content"
                    // value={username}
                    onChange={(e) => setCont(e.target.value)}
                />
            </div>
            <input
                type="submit"
                value="Add file"
                className="btn btn-block"
                readOnly
            />
        </form>
    );
};

export default AddFile;
