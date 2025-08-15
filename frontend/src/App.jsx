import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Dashboard from './pages/Dashboard';
import Groups from './pages/Groups';
import CreateGroup from './pages/Group/CreateGroup';
import ProtectedRoute from './components/ProtectedRoutes';
import { AuthProvider } from './context/AuthContext';
import { Navigate } from 'react-router-dom';
import Profile from './components/Profile';
import UpdateProfile from './components/UpdateProfile';
import GroupDetails from './pages/GroupDetails';
import AddExpense from './pages/Group/AddExpense';
import SettleForm from './components/group/SettleForm';
import EditExpense from './pages/Group/EditExpense';
import OAuth2RedirectHandler from './utils/OAuth2RedirectHandler';


function App() {
  return (
    <Router>
      <AuthProvider>
        <Routes>
          {/* <Route path="" element={<Login />} /> */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
          <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
           <Route path="profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
           <Route path="update" element={<ProtectedRoute><UpdateProfile /></ProtectedRoute>} />
          <Route path="/groups" element= {<ProtectedRoute><Groups /></ProtectedRoute>} />
          <Route path="/create-group" element={<CreateGroup />} />
          <Route path="/group/:groupid" element={<ProtectedRoute><GroupDetails /></ProtectedRoute>} />
          <Route path="/group/:groupid/add-expense" element={<ProtectedRoute><AddExpense /></ProtectedRoute>} />
          <Route path="/settle" element={<ProtectedRoute><SettleForm /></ProtectedRoute>} />
          <Route path="/group/:groupid/expenses/:expenseid/edit" element={<EditExpense />} />


        </Routes>
      </AuthProvider>
    </Router>
  );
}

export default App;
