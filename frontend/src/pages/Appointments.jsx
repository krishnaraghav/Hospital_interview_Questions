import { useEffect, useState } from 'react'
import { api, formatDate, formatTime } from '../api/client'
import Badge from '../components/Badge'
import Modal from '../components/Modal'

const today = new Date().toISOString().slice(0, 10)
const empty = { patientId: '', doctorId: '', appointmentDate: today, appointmentTime: '09:00', reason: '', status: 'SCHEDULED' }
const filters = ['ALL', 'SCHEDULED', 'COMPLETED', 'CANCELLED']

export default function Appointments() {
  const [rows, setRows] = useState([])
  const [patients, setPatients] = useState([])
  const [doctors, setDoctors] = useState([])
  const [filter, setFilter] = useState('ALL')
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () => Promise.all([api.appointments.list(), api.patients.list(), api.doctors.list()]).then(([visits, patientRows, doctorRows]) => {
    setRows(visits)
    setPatients(patientRows)
    setDoctors(doctorRows)
  }).catch((err) => setError(err.message))

  useEffect(() => { load() }, [])

  const visible = rows.filter((row) => filter === 'ALL' || row.status === filter)

  function setField(key, value) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function save(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = { ...form, patientId: Number(form.patientId), doctorId: Number(form.doctorId) }
    try {
      if (form.id) await api.appointments.update(form.id, payload)
      else await api.appointments.create(payload)
      setForm(null)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function remove(row) {
    if (!window.confirm('Cancel and remove this appointment?')) return
    try {
      await api.appointments.remove(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Schedule</div>
          <h1>Appointments</h1>
          <p className="lede">Clinic slots across departments.</p>
        </div>
        <button className="btn" type="button" onClick={() => setForm(empty)}>Book visit</button>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      <div className="toolbar">
        <div className="chips">
          {filters.map((item) => (
            <button key={item} className={filter === item ? 'chip on' : 'chip'} type="button" onClick={() => setFilter(item)}>
              {item === 'ALL' ? 'All' : item.charAt(0) + item.slice(1).toLowerCase()}
            </button>
          ))}
        </div>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>When</th><th>Patient</th><th>Doctor</th><th>Reason</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            {visible.length === 0 ? <tr><td colSpan="6" className="empty">No appointments in this view.</td></tr> : visible.map((row) => (
              <tr key={row.id}>
                <td><strong>{formatTime(row.appointmentTime)}</strong><div className="muted">{formatDate(row.appointmentDate)}</div></td>
                <td>{row.patientName}</td>
                <td>{row.doctorName}<div className="muted">{row.specialization}</div></td>
                <td>{row.reason || '—'}</td>
                <td><Badge value={row.status} /></td>
                <td>
                  <div className="row-actions">
                    <button className="btn-ghost" type="button" onClick={() => setForm({ ...row, appointmentTime: row.appointmentTime?.slice(0, 5) })}>Edit</button>
                    <button className="btn-danger" type="button" onClick={() => remove(row)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {form ? (
        <Modal title={form.id ? 'Edit appointment' : 'Book appointment'} subtitle="Choose a patient, doctor, and slot." onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="form-grid">
              <Field label="Patient">
                <select required value={form.patientId} onChange={(e) => setField('patientId', e.target.value)}>
                  <option value="">Select</option>
                  {patients.map((item) => <option key={item.id} value={item.id}>{item.fullName}</option>)}
                </select>
              </Field>
              <Field label="Doctor">
                <select required value={form.doctorId} onChange={(e) => setField('doctorId', e.target.value)}>
                  <option value="">Select</option>
                  {doctors.map((item) => <option key={item.id} value={item.id}>{item.fullName} · {item.specialization}</option>)}
                </select>
              </Field>
              <Field label="Date"><input required type="date" value={form.appointmentDate} onChange={(e) => setField('appointmentDate', e.target.value)} /></Field>
              <Field label="Time"><input required type="time" value={form.appointmentTime} onChange={(e) => setField('appointmentTime', e.target.value)} /></Field>
              <Field label="Status">
                <select value={form.status} onChange={(e) => setField('status', e.target.value)}>
                  <option value="SCHEDULED">Scheduled</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="CANCELLED">Cancelled</option>
                </select>
              </Field>
              <Field label="Reason" wide><textarea rows="3" value={form.reason || ''} onChange={(e) => setField('reason', e.target.value)} /></Field>
            </div>
            <div className="form-actions">
              <button className="btn-ghost" type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="btn" disabled={saving}>{saving ? 'Saving…' : 'Save appointment'}</button>
            </div>
          </form>
        </Modal>
      ) : null}
    </>
  )
}

function Field({ label, wide, children }) {
  return <label className={wide ? 'field wide' : 'field'}><span>{label}</span>{children}</label>
}
