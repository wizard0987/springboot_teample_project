import { useState, useEffect } from "react"
import { NavLink } from "react-router-dom"
import axios from "axios";
import { genreMap } from 'feature/GenreMap'

import { Image, Table } from "react-bootstrap";
import styled from "styled-components";

const BestBookList = () => {
  const [books, setBooks] = useState([]);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/index/bestitems/`)
      .then(({data: { _embedded: { defaultList }}}) => setBooks(defaultList))
      .catch(err => console.log(err))
  }, [])
 
  return <>
    {books.length && <Container responsive="sm">
    <thead>
      <tr>
      {["포스터이미지", "제목", "장르", "출판사", "가격"].map((tag, idx) => (
        <th key={idx}><ItemContent>{tag}</ItemContent></th>
      ))}
      </tr>
      </thead>
      <tbody>
        {books.map((book, idx) => (
          <tr key={idx}>
            <td><NavButton to={{pathname:`/detail/${book.id}`, state:{book}}}>
              <ItemImage src={book.imageUrl}/>
            </NavButton></td>
            <td><ItemContent>{book.name}</ItemContent></td>
            <td><ItemContent>{genreMap[book.category_id]}</ItemContent></td>
            <td><ItemContent>{book.publisher}</ItemContent></td>
            <td><ItemContent>{book.price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}</ItemContent></td>
          </tr>
        ))}
      </tbody>
    </Container>
    }</>
};
export default BestBookList;

const Container = styled(Table)`
  margin: 0 auto;
  width: 80%;

  td {
    vertical-align: middle; 
    font-weight: 600;
  } 
`

const ItemImage = styled(Image)`
  width: 180px;
  height: 220px;
  object-fit: cover;
`
const NavButton = styled(NavLink)`
  margin: 0 10% 0 0;
  float: right;
`

const ItemContent = styled.div`
  font-size: 20px;
  text-align: center;

  margin: auto 0;
`