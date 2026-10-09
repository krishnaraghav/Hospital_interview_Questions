import { useEffect, useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'

const links = [
  ['/', 'Overview', 'grid'],
  ['/patients', 'Patients', 'users'],
  ['/doctors', 'Doctors', 'pulse'],
  ['/appointments', 'Appointments', 'calendar'],
  ['/departments', 'Departments', 'building'],
  ['/billing', 'Billing', 'bill'],
]

function Icon({ name }) {
  const common = {
    viewBox: '0 0 24 24',
    fill: 'none',
    stroke: 'currentColor',
    strokeWidth: 1.8,
    strokeLinecap: 'round',
    strokeLinejoin: 'round',
    'aria-hidden': true,
  }
  if (name === 'grid') {
    return (
      <svg {...common}>
        <rect x="3" y="3" width="7" height="7" rx="1.5" />
        <rect x="14" y="3" width="7" height="7" rx="1.5" />
        <rect x="3" y="14" width="7" height="7" rx="1.5" />
        <rect x="14" y="14" width="7" height="7" rx="1.5" />
      </svg>
    )
  }
  if (name === 'users') {
    return (
      <svg {...common}>
        <path d="M16 20v-1.5a3.5 3.5 0 0 0-3.5-3.5h-5A3.5 3.5 0 0 0 4 18.5V20" />
        <circle cx="9" cy="8" r="3" />
        <path d="M20 20v-1.2a3 3 0 0 0-2.2-2.9" />
        <path d="M16 5.1a3 3 0 0 1 0 5.8" />
      </svg>
    )
  }
  if (name === 'pulse') {
    return (
      <svg {...common}>
        <path d="M3 12h4l2-5 4 10 2-5h6" />
      </svg>
    )
  }
  if (name === 'calendar') {
    return (
      <svg {...common}>
        <rect x="3" y="5" width="18" height="16" rx="2" />
        <path d="M8 3v4M16 3v4M3 10h18" />
      </svg>
    )
  }
  if (name === 'building') {
    return (
      <svg {...common}>
        <path d="M4 20h16M6 20V5h8v15M14 9h4v11" />
        <path d="M8.5 8h3M8.5 12h3M8.5 16h3" />
      </svg>
    )
  }
  if (name === 'cross') {
    return (
      <svg {...common}>
        <path d="M12 5v14M5 12h14" />
      </svg>
    )
  }
  return (
    <svg {...common}>
      <rect x="4" y="3" width="16" height="18" rx="2" />
      <path d="M8 8h8M8 12h8M8 16h5" />
    </svg>
  )
}

export default function Layout() {
  const [open, setOpen] = useState(false)
  const location = useLocation()
  useEffect(() => setOpen(false), [location.pathname])

  const today = new Date().toLocaleDateString('en-IN', {
    weekday: 'long',
    day: 'numeric',
    month: 'long',
  })

  return (
    <div className="shell">
      <aside className={open ? 'sidebar open' : 'sidebar'}>
        <div className="brand">
          <div className="mark"><Icon name="cross" /></div>
          <div>
            <strong>AetherCare</strong>
            <small>HOSPITAL</small>
          </div>
        </div>
        <nav className="nav">
          {links.map(([to, label, icon]) => (
            <NavLink key={to} to={to} end={to === '/'}>
              <Icon name={icon} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="side-note">
          <strong>Main campus</strong>
          <p>Front desk, wards, and clinics share one live record.</p>
        </div>
      </aside>
      <div className="workspace">
        <header className="topbar">
          <button className="icon-btn menu" type="button" aria-label="Open menu" onClick={() => setOpen((value) => !value)}>
            <Icon name="grid" />
          </button>
          <div className="campus">Main campus · {today}</div>
          <div className="user-pill">
            <span className="avatar">FD</span>
            <div>
              <strong>Front desk</strong>
              <small>Signed in</small>
            </div>
          </div>
        </header>
        <main>
          <Outlet />
        </main>
      </div>
    </div>
  )
}
