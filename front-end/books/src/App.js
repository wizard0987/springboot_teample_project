import Header from "./components/HeaderPage/Header"
import Home from "./components/MainPage/HomePage/Home"
import ItemDetail from "./components/MainPage/DetailPage/ItemDetail"
import BookList from "./components/MainPage/BookListPage/BookList"
import Community from "./components/MainPage/ComunityPage/Community"
import CommunityRegister from "./components/MainPage/ComunityPage/CommunityRegister"
import CommunityDetail from "./components/MainPage/ComunityPage/CommunityDetail"
import Footer from "./components/FooterPage/Footer"
import LoginPage from "./components/UserPage/LoginPage"
import SignupPage from "./components/UserPage/SignupPage"
import DefaultPage from "./DefaultPage"

import {useState} from "react"
import { Route, Switch } from "react-router-dom"
import ScrollToTop from "./ScrollToTop"

import styled from "styled-components"


const App = () => {
  const [reRender, setReRender] = useState(false);

  const doRendering = () => {
    setReRender(prev => !prev)
  }

  return <AppContainer>
    <ScrollToTop />
    <DefaultPage state={reRender} dispatch={doRendering} />
    <Header />
    <Switch>
      <Route exact path="/" component={Home} />
      <Route path="/detail" component={ItemDetail} />
      <Route path="/booklist" component={BookList} />
      <Route exact path="/community" component={Community} />
      <Route path="/community/detail/:id" component={CommunityDetail} />
      <Route path="/community/register" component={CommunityRegister} />
      <Route path="/login" component={LoginPage} />
      <Route path="/signup" component={SignupPage} />
    </Switch>
    <Footer />
  </AppContainer>
}
export default App;

const AppContainer = styled.div`
  margin: 20px 8%;
  height: 100vh;
`