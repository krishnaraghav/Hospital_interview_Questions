import { useEffect, useState } from 'react'
import { api, initials, money } from '../api/client'
import Badge from '../components/Badge'
import Modal from '../components/Modal'

const empty = { fullName: '', specialization: '', email: '', phone: '', departmentId: '', experienceYears: '', consultationFee: '', status: 'AVAILABLE' }

export default function Doctors() {
  const [rows, setRows] = useState([])
  const [departments, setDepartments] = useState([])
  const [query, setQuery] = useState('')
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () => Promise.all([api.doctors.list(), api.departments.list()]).then(([doctorRows, departmentRows]) => {
    setRows(doctorRows)
    setDepartments(departmentRows)
  }).catch((err) => setError(err.message))

  useEffect(() => { load() }, [])

  const visible = rows.filter((row) => `${row.fullName} ${row.specialization} ${row.departmentName}`.toLowerCase().includes(query.toLowerCase()))

  function setField(key, value) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function save(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = {
      ...form,
      departmentId: Number(form.departmentId),
      experienceYears: Number(form.experienceYears),
      consultationFee: Number(form.consultationFee),
    }
    try {
      if (form.id) await api.doctors.update(form.id, payload)
      else await api.doctors.create(payload)
      setForm(null)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function remove(row) {
    if (!window.confirm(`Remove ${row.fullName} from the roster?`)) return
    try {
      await api.doctors.remove(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Clinical staff</div>
          <h1>Doctors</h1>
          <p className="lede">Specialties, duty status, and consultation fees.</p>
        </div>
        <button className="btn" type="button" onClick={() => setForm({ ...empty, departmentId: departments[0]?.id || '' })}>Add doctor</button>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      <div className="toolbar">
        <input className="search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search name, specialty, department" />
      </div>
      <section className="card-grid">
        {visible.map((row) => (
          <article className="person" key={row.id}>
            <div className="person-top">
              <div className="who">
                <span className="bubble">{initials(row.fullName)}</span>
                <div>
                  <strong>{row.fullName}</strong>
                  <p>{row.specialization}</p>
                </div>
              </div>
              <Badge value={row.status} />
            </div>
            <div className="meta">
              <span>{row.departmentName}</span>
              <span>{row.experienceYears} yrs</span>
              <b>{money(row.consultationFee)}</b>
            </div>
            <p>{row.phone}</p>
            <div className="row-actions" style={{ marginTop: 14 }}>
              <button className="btn-ghost" type="button" onClick={() => setForm(row)}>Edit</button>
              <button className="btn-danger" type="button" onClick={() => remove(row)}>Delete</button>
            </div>
          </article>
        ))}
      </section>
      {form ? (
        <Modal title={form.id ? 'Edit doctor' : 'New doctor'} subtitle="Assign a department and duty status." onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="form-grid">
              <Field label="Full name"><input required value={form.fullName} onChange={(e) => setField('fullName', e.target.value)} /></Field>
              <Field label="Specialization"><input required value={form.specialization} onChange={(e) => setField('specialization', e.target.value)} /></Field>
              <Field label="Department">
                <select required value={form.departmentId} onChange={(e) => setField('departmentId', e.target.value)}>
                  <option value="">Select</option>
                  {departments.map((item) => <option key={item.id} value={item.id}>{item.name}</option>)}
                </select>
              </Field>
              <Field label="Phone"><input required value={form.phone} onChange={(e) => setField('phone', e.target.value)} /></Field>
              <Field label="Email"><input type="email" value={form.email || ''} onChange={(e) => setField('email', e.target.value)} /></Field>
              <Field label="Experience (years)"><input required type="number" min="0" max="60" value={form.experienceYears} onChange={(e) => setField('experienceYears', e.target.value)} /></Field>
              <Field label="Consultation fee"><input required type="number" min="0" value={form.consultationFee} onChange={(e) => setField('consultationFee', e.target.value)} /></Field>
              <Field label="Status">
                <select value={form.status} onChange={(e) => setField('status', e.target.value)}>
                  <option value="AVAILABLE">Available</option>
                  <option value="IN_ROUNDS">In rounds</option>
                  <option value="ON_LEAVE">On leave</option>
                </select>
              </Field>
            </div>
            <div className="form-actions">
              <button className="btn-ghost" type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="btn" disabled={saving}>{saving ? 'Saving…' : 'Save doctor'}</button>
            </div>
          </form>
        </Modal>
      ) : null}
    </>
  )
}

function Field({ label, children }) {
  return <label className="field"><span>{label}</span>{children}</label>
}
