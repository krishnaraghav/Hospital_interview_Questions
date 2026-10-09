import { useEffect, useState } from 'react'
import { api } from '../api/client'
import Modal from '../components/Modal'

const empty = { name: '', description: '', floor: '', phone: '' }

export default function Departments() {
  const [rows, setRows] = useState([])
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () => api.departments.list().then(setRows).catch((err) => setError(err.message))
  useEffect(() => { load() }, [])

  function setField(key, value) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function save(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    try {
      if (form.id) await api.departments.update(form.id, form)
      else await api.departments.create(form)
      setForm(null)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function remove(row) {
    if (!window.confirm(`Remove ${row.name}?`)) return
    try {
      await api.departments.remove(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Campus map</div>
          <h1>Departments</h1>
          <p className="lede">Floors, phones, and the doctors assigned to each unit.</p>
        </div>
        <button className="btn" type="button" onClick={() => setForm(empty)}>Add department</button>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      <section className="dept-grid">
        {rows.map((row) => (
          <article className="dept" key={row.id}>
            <div className="dept-top">
              <div>
                <strong>{row.name}</strong>
                <p>{row.floor || 'Floor not set'} · {row.phone || 'No phone'}</p>
              </div>
              <span className="badge tone-blue">{row.doctorCount} doctors</span>
            </div>
            <p style={{ marginTop: 12 }}>{row.description || 'No description yet.'}</p>
            <div className="row-actions" style={{ marginTop: 14 }}>
              <button className="btn-ghost" type="button" onClick={() => setForm(row)}>Edit</button>
              <button className="btn-danger" type="button" onClick={() => remove(row)}>Delete</button>
            </div>
          </article>
        ))}
      </section>
      {form ? (
        <Modal title={form.id ? 'Edit department' : 'New department'} subtitle="Name the unit and where it sits in the building." onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="form-grid">
              <Field label="Name"><input required value={form.name} onChange={(e) => setField('name', e.target.value)} /></Field>
              <Field label="Floor"><input value={form.floor || ''} onChange={(e) => setField('floor', e.target.value)} /></Field>
              <Field label="Phone"><input value={form.phone || ''} onChange={(e) => setField('phone', e.target.value)} /></Field>
              <Field label="Description" wide><textarea rows="3" value={form.description || ''} onChange={(e) => setField('description', e.target.value)} /></Field>
            </div>
            <div className="form-actions">
              <button className="btn-ghost" type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="btn" disabled={saving}>{saving ? 'Saving…' : 'Save department'}</button>
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
