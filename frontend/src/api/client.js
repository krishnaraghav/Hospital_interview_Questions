async function request(path, options = {}) {
  let response
  try {
    response = await fetch(path, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options.headers || {}),
      },
    })
  } catch {
    throw new Error('Cannot reach the hospital API. Start the Spring Boot server on port 8080.')
  }

  if (response.status === 204) return null
  const data = await response.json().catch(() => ({}))
  if (!response.ok) {
    throw new Error(data.message || 'Request failed')
  }
  return data
}

export const api = {
  dashboard: () => request('/api/dashboard'),
  departments: {
    list: () => request('/api/departments'),
    create: (body) => request('/api/departments', { method: 'POST', body: JSON.stringify(body) }),
    update: (id, body) => request(`/api/departments/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
    remove: (id) => request(`/api/departments/${id}`, { method: 'DELETE' }),
  },
  doctors: {
    list: () => request('/api/doctors'),
    create: (body) => request('/api/doctors', { method: 'POST', body: JSON.stringify(body) }),
    update: (id, body) => request(`/api/doctors/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
    remove: (id) => request(`/api/doctors/${id}`, { method: 'DELETE' }),
  },
  patients: {
    list: () => request('/api/patients'),
    create: (body) => request('/api/patients', { method: 'POST', body: JSON.stringify(body) }),
    update: (id, body) => request(`/api/patients/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
    remove: (id) => request(`/api/patients/${id}`, { method: 'DELETE' }),
  },
  appointments: {
    list: () => request('/api/appointments'),
    create: (body) => request('/api/appointments', { method: 'POST', body: JSON.stringify(body) }),
    update: (id, body) => request(`/api/appointments/${id}`, { method: 'PUT', body: JSON.stringify(body) }),
    remove: (id) => request(`/api/appointments/${id}`, { method: 'DELETE' }),
  },
  invoices: {
    list: () => request('/api/invoices'),
    create: (body) => request('/api/invoices', { method: 'POST', body: JSON.stringify(body) }),
    pay: (id) => request(`/api/invoices/${id}/pay`, { method: 'POST' }),
    remove: (id) => request(`/api/invoices/${id}`, { method: 'DELETE' }),
  },
}

export function money(value) {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(Number(value || 0))
}

export function formatDate(value) {
  if (!value) return '—'
  const [year, month, day] = value.split('-').map(Number)
  return new Date(year, month - 1, day).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    year: 'numeric',
  })
}

export function formatTime(value) {
  if (!value) return '—'
  const [hour, minute] = value.split(':').map(Number)
  const date = new Date()
  date.setHours(hour, minute || 0, 0, 0)
  return date.toLocaleTimeString('en-IN', { hour: 'numeric', minute: '2-digit' })
}

export function initials(name = '') {
  const parts = name.replace(/^Dr\.\s*/i, '').split(/\s+/).filter(Boolean)
  return `${parts[0]?.[0] || ''}${parts[1]?.[0] || ''}`.toUpperCase()
}
