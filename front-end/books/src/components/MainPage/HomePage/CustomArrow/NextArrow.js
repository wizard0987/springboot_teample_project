import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faArrowCircleRight } from '@fortawesome/free-solid-svg-icons'
import "./NextArrow.css"

const NextArrow = (props) => {

  const { onClick } = props
  return <button
    className="promotion-next-btn"
    onClick={onClick}
  ><FontAwesomeIcon icon={faArrowCircleRight} style={{color:"rgb(43, 43, 48)"}}/></button>
}
export default NextArrow