import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout'
import Appointments from './pages/Appointments'
import Billing from './pages/Billing'
import Dashboard from './pages/Dashboard'
import Departments from './pages/Departments'
import Doctors from './pages/Doctors'
import Patients from './pages/Patients'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<Dashboard />} />
        <Route path="/patients" element={<Patients />} />
        <Route path="/doctors" element={<Doctors />} />
        <Route path="/appointments" element={<Appointments />} />
        <Route path="/departments" element={<Departments />} />
        <Route path="/billing" element={<Billing />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  )
}
