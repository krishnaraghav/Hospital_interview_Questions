const tones = {
  ADMITTED: 'blue',
  SCHEDULED: 'blue',
  IN_ROUNDS: 'blue',
  AVAILABLE: 'green',
  COMPLETED: 'green',
  PAID: 'green',
  OUTPATIENT: 'slate',
  DISCHARGED: 'slate',
  ON_LEAVE: 'amber',
  PENDING: 'amber',
  CANCELLED: 'rose',
}

export default function Badge({ value }) {
  const tone = tones[value] || 'slate'
  return <span className={`badge tone-${tone}`}>{(value || '').replaceAll('_', ' ')}</span>
}
