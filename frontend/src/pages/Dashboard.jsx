import { useEffect, useState } from 'react'
import { api, formatDate, formatTime, money } from '../api/client'
import Badge from '../components/Badge'

export default function Dashboard() {
  const [data, setData] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.dashboard().then(setData).catch((err) => setError(err.message))
  }, [])

  const hour = new Date().getHours()
  const greeting = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening'
  const peak = Math.max(1, ...(data?.departments || []).map((item) => item.appointments))

  return (
    <>
      <div className="page-head">
        <div>
          <div className="eyebrow">Care overview</div>
          <h1>{greeting}</h1>
          <p className="lede">Wards, clinics, and billing for the main campus.</p>
        </div>
      </div>
      {error ? <div className="alert">{error}</div> : null}
      {!data && !error ? <div className="skeleton" /> : null}
      {data ? (
        <>
          <section className="stats">
            <article className="stat"><span>Patients</span><strong>{data.patients}</strong><em>{data.admitted} admitted</em></article>
            <article className="stat"><span>Doctors on duty</span><strong>{data.onDuty}</strong><em>{data.doctors} on staff</em></article>
            <article className="stat"><span>Today’s visits</span><strong>{data.appointmentsToday}</strong><em>Not cancelled</em></article>
            <article className="stat"><span>Collected</span><strong>{money(data.revenueCollected)}</strong><em>{data.pendingInvoices} bills open</em></article>
          </section>
          <section className="split">
            <article className="panel">
              <h2>Upcoming appointments</h2>
              {data.upcoming.length === 0 ? <p className="empty">No upcoming visits.</p> : data.upcoming.map((visit) => (
                <div className="visit" key={visit.id}>
                  <div className="when">{formatTime(visit.appointmentTime)}<br />{formatDate(visit.appointmentDate)}</div>
                  <div>
                    <strong>{visit.patientName}</strong>
                    <p>{visit.doctorName} · {visit.specialization}</p>
                  </div>
                  <Badge value={visit.status} />
                </div>
              ))}
            </article>
            <div>
              <article className="panel" style={{ marginBottom: 14 }}>
                <h2>Scheduled load</h2>
                {data.departments.map((item) => (
                  <div className="bar-row" key={item.name}>
                    <span>{item.name}</span>
                    <div className="track"><i style={{ width: `${(item.appointments / peak) * 100}%` }} /></div>
                    <b>{item.appointments}</b>
                  </div>
                ))}
              </article>
              <article className="panel">
                <h2>Recent records</h2>
                {data.recentPatients.map((patient) => (
                  <div className="person-row" key={patient.id}>
                    <div>
                      <strong>{patient.fullName}</strong>
                      <p>{patient.gender} · {patient.age} yrs · {patient.room || 'No room'}</p>
                    </div>
                    <Badge value={patient.status} />
                  </div>
                ))}
              </article>
            </div>
          </section>
        </>
      ) : null}
    </>
  )
}
