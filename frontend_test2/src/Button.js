import "./Button.css";
function Button({ children, onClick, className = "" }) {
  const classNames = `Button ${className}`;
  return (
    <button className={classNames} onClick={onClick}>
      {children}
    </button>
  );
}

export default Button;
