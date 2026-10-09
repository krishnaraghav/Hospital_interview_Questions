import { useEffect, useState } from 'react'
import { api, formatDate, initials } from '../api/client'
import Badge from '../components/Badge'
import Modal from '../components/Modal'

const empty = { fullName: '', age: '', gender: 'Female', email: '', phone: '', bloodGroup: 'O+', address: '', room: '', status: 'OUTPATIENT' }

export default function Patients() {
  const [rows, setRows] = useState([])
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)
  const [loading, setLoading] = useState(true)

  const load = () => api.patients.list().then(setRows).catch((err) => setError(err.message)).finally(() => setLoading(false))
  useEffect(() => { load() }, [])

  const visible = rows.filter((row) => `${row.fullName} ${row.phone} ${row.bloodGroup}`.toLowerCase().includes(query.toLowerCase()))

  function setField(key, value) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function save(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = { ...form, age: Number(form.age) }
    try {
      if (form.id) await api.patients.update(form.id, payload)
      else await api.patients.create(payload)
      setForm(null)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function remove(row) {
    if (!window.confirm(`Remove ${row.fullName} from the register?`)) return
    try {
      await api.patients.remove(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Register</div>
          <h1>Patients</h1>
          <p className="lede">Admissions, outpatient visits, and room assignments.</p>
        </div>
        <button className="btn" type="button" onClick={() => setForm(empty)}>Add patient</button>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      <div className="toolbar">
        <input className="search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search name, phone, blood group" />
      </div>
      {loading ? <div className="skeleton" /> : (
        <div className="table-wrap">
          <table>
            <thead>
              <tr><th>Patient</th><th>Contact</th><th>Blood</th><th>Room</th><th>Status</th><th></th></tr>
            </thead>
            <tbody>
              {visible.length === 0 ? <tr><td colSpan="6" className="empty">No patients match this search.</td></tr> : visible.map((row) => (
                <tr key={row.id}>
                  <td>
                    <div className="who">
                      <span className="bubble">{initials(row.fullName)}</span>
                      <div><strong>{row.fullName}</strong><div className="muted">{row.gender} · {row.age} yrs · {formatDate(row.registeredOn)}</div></div>
                    </div>
                  </td>
                  <td>{row.phone}<div className="muted">{row.email || 'No email'}</div></td>
                  <td>{row.bloodGroup || '—'}</td>
                  <td>{row.room || '—'}</td>
                  <td><Badge value={row.status} /></td>
                  <td>
                    <div className="row-actions">
                      <button className="btn-ghost" type="button" onClick={() => setForm(row)}>Edit</button>
                      <button className="btn-danger" type="button" onClick={() => remove(row)}>Delete</button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {form ? (
        <Modal title={form.id ? 'Edit patient' : 'New patient'} subtitle="Keep the ward register current." onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="form-grid">
              <Field label="Full name"><input required value={form.fullName} onChange={(e) => setField('fullName', e.target.value)} /></Field>
              <Field label="Age"><input required type="number" min="0" max="130" value={form.age} onChange={(e) => setField('age', e.target.value)} /></Field>
              <Field label="Gender">
                <select value={form.gender} onChange={(e) => setField('gender', e.target.value)}>
                  <option>Female</option><option>Male</option><option>Other</option>
                </select>
              </Field>
              <Field label="Phone"><input required value={form.phone} onChange={(e) => setField('phone', e.target.value)} /></Field>
              <Field label="Email"><input type="email" value={form.email || ''} onChange={(e) => setField('email', e.target.value)} /></Field>
              <Field label="Blood group">
                <select value={form.bloodGroup || ''} onChange={(e) => setField('bloodGroup', e.target.value)}>
                  {['A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-'].map((group) => <option key={group}>{group}</option>)}
                </select>
              </Field>
              <Field label="Room"><input value={form.room || ''} onChange={(e) => setField('room', e.target.value)} placeholder="A-214" /></Field>
              <Field label="Status">
                <select value={form.status} onChange={(e) => setField('status', e.target.value)}>
                  <option value="OUTPATIENT">Outpatient</option>
                  <option value="ADMITTED">Admitted</option>
                  <option value="DISCHARGED">Discharged</option>
                </select>
              </Field>
              <Field label="Address" wide><textarea rows="3" value={form.address || ''} onChange={(e) => setField('address', e.target.value)} /></Field>
            </div>
            <div className="form-actions">
              <button className="btn-ghost" type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="btn" type="submit" disabled={saving}>{saving ? 'Saving…' : 'Save patient'}</button>
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
