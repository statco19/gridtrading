import { useState } from "react";
import { Link } from "react-router-dom";
import { startTrading } from "./api";
import "./LimitForm.css";

function sanitize(type, value) {
  switch (type) {
    case "number":
      return Number(value) || 0;

    default:
      return value;
  }
}

const INITIAL_VALUES = {
  accessKey: "엑",
  secretKey: "secretkey",
  lowerPrice: 90,
  budget: 100,
  gridNum: 0,
  lower: 0,
};

function LimitForm({
  initialValues = INITIAL_VALUES,
  onSubmit,
  onSubmitSuccess,
}) {
  const [values, setValues] = useState(initialValues);
  const [submittingError, setSubmittingError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  //   const [upperPrice, setUppePrice] = useState("100");
  //   const [lowerPrice, setLowerPrice] = useState("90");
  // const handleUpperPriceChange = (e) => setUppePrice(e.target.value);
  const handleSubmit = async (e) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append("accessKey", values.accessKey);
    formData.append("lowerPrice", values.lowerPrice);
    formData.append("budget", values.budget);
    formData.append("gridNum", values.gridNum);
    let result;
    try {
      setSubmittingError(null);
      setIsSubmitting(true);
      console.log("트라이구문");
      result = await onSubmit(formData);
    } catch (error) {
      setSubmittingError(error);
      return;
    } finally {
      setIsSubmitting(false);
    }
    const { food } = result;
    setValues(initialValues);
    onSubmitSuccess(food);
  };

  const handleChange = (name, value) => {
    setValues((prevValues) => ({
      ...prevValues,
      [name]: value,
    }));
  };

  const handleInputChange = (e) => {
    const { name, value, type } = e.target;
    handleChange(name, sanitize(type, value));
  };

  return (
    <form className="LimitForm" onSubmit={handleSubmit}>
      <div className="LimitForm-columns">
        <div className="LimitForm-rows">
          <label htmlFor="accessKey">accessKey</label>
          <input
            className="LimitForm-accessKey"
            id="accessKey"
            name="accessKey"
            value={values.accessKey}
            placeholder="accessKey"
            onChange={handleInputChange}
          />
        </div>
        <div className="LimitForm-rows">
          <label htmlFor="secretKey">secretKey</label>
          <input
            className="LimitForm-secretKey"
            id="secretKey"
            name="secretKey"
            value={values.secretKey}
            placeholder="secretKey"
            onChange={handleInputChange}
          />
        </div>
        <div className="LimitForm-rows">
          <label htmlFor="lowerPrice">Stoploss (하한)</label>
          <input
            className="LimitForm-lower"
            id="lowerPrice"
            name="lowerPrice"
            value={values.lowerPrice}
            placeholder="하한"
            onChange={handleInputChange}
          />
        </div>
        <div className="LimitForm-rows">
          <label htmlFor="lowerPrice">Budget (예산) </label>
          <input
            className="LimitForm-budget"
            id="budget"
            name="budget"
            value={values.budget}
            placeholder="예산"
            onChange={handleInputChange}
          />
        </div>
        <div className="LimitForm-rows">
          <label htmlFor="gridNum">Grid Number </label>
          <input
            className="LimitForm-budget"
            id="gridNum"
            name="gridNum"
            value={values.gridNum}
            placeholder="그리드 수"
            onChange={handleInputChange}
          />
        </div>
        <div className="LimitForm-rows">
          <label htmlFor="lower">lower </label>
          <input
            className="LimitForm-lower"
            id="lower"
            name="lower"
            value={values.lower}
            placeholder="lower"
            onChange={handleInputChange}
          />
        </div>
        <Link to="/results">
          <button
            className="LimitForm-submit-button"
            type="submit"
            disabled={isSubmitting}
            onClick={startTrading}
          >
            테스트 확인
          </button>
        </Link>
        {submittingError && <p>{submittingError.message}</p>}
      </div>
    </form>
  );
}

export default LimitForm;
