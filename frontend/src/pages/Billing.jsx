import { useEffect, useState } from 'react'
import { api, formatDate, money } from '../api/client'
import Badge from '../components/Badge'
import Modal from '../components/Modal'

const empty = { patientId: '', description: '', amount: '', status: 'PENDING' }

export default function Billing() {
  const [rows, setRows] = useState([])
  const [patients, setPatients] = useState([])
  const [form, setForm] = useState(null)
  const [error, setError] = useState('')
  const [saving, setSaving] = useState(false)

  const load = () => Promise.all([api.invoices.list(), api.patients.list()]).then(([invoiceRows, patientRows]) => {
    setRows(invoiceRows)
    setPatients(patientRows)
  }).catch((err) => setError(err.message))

  useEffect(() => { load() }, [])

  const pending = rows.filter((row) => row.status === 'PENDING').reduce((sum, row) => sum + Number(row.amount), 0)

  function setField(key, value) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  async function save(event) {
    event.preventDefault()
    setSaving(true)
    setError('')
    const payload = { ...form, patientId: Number(form.patientId), amount: Number(form.amount) }
    try {
      await api.invoices.create(payload)
      setForm(null)
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setSaving(false)
    }
  }

  async function pay(row) {
    try {
      await api.invoices.pay(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  async function remove(row) {
    if (!window.confirm(`Delete ${row.invoiceNo}?`)) return
    try {
      await api.invoices.remove(row.id)
      await load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Revenue</div>
          <h1>Billing</h1>
          <p className="lede">{money(pending)} still open across unpaid invoices.</p>
        </div>
        <button className="btn" type="button" onClick={() => setForm(empty)}>New invoice</button>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      <div className="table-wrap">
        <table>
          <thead>
            <tr><th>Invoice</th><th>Patient</th><th>Description</th><th>Amount</th><th>Status</th><th></th></tr>
          </thead>
          <tbody>
            {rows.length === 0 ? <tr><td colSpan="6" className="empty">No invoices yet.</td></tr> : rows.map((row) => (
              <tr key={row.id}>
                <td><strong>{row.invoiceNo}</strong><div className="muted">{formatDate(row.issuedOn)}</div></td>
                <td>{row.patientName}</td>
                <td>{row.description}</td>
                <td>{money(row.amount)}</td>
                <td><Badge value={row.status} /></td>
                <td>
                  <div className="row-actions">
                    {row.status === 'PENDING' ? <button className="btn" type="button" onClick={() => pay(row)}>Mark paid</button> : null}
                    <button className="btn-danger" type="button" onClick={() => remove(row)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {form ? (
        <Modal title="New invoice" subtitle="A number is assigned when the bill is saved." onClose={() => setForm(null)}>
          <form onSubmit={save}>
            <div className="form-grid">
              <Field label="Patient">
                <select required value={form.patientId} onChange={(e) => setField('patientId', e.target.value)}>
                  <option value="">Select</option>
                  {patients.map((item) => <option key={item.id} value={item.id}>{item.fullName}</option>)}
                </select>
              </Field>
              <Field label="Amount (INR)"><input required type="number" min="0" step="1" value={form.amount} onChange={(e) => setField('amount', e.target.value)} /></Field>
              <Field label="Status">
                <select value={form.status} onChange={(e) => setField('status', e.target.value)}>
                  <option value="PENDING">Pending</option>
                  <option value="PAID">Paid</option>
                </select>
              </Field>
              <Field label="Description" wide><textarea required rows="3" value={form.description} onChange={(e) => setField('description', e.target.value)} /></Field>
            </div>
            <div className="form-actions">
              <button className="btn-ghost" type="button" onClick={() => setForm(null)}>Cancel</button>
              <button className="btn" disabled={saving}>{saving ? 'Saving…' : 'Create invoice'}</button>
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
