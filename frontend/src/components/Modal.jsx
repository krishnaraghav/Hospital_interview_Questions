export default function Modal({ title, subtitle, onClose, children }) {
  return (
    <div className="backdrop" onMouseDown={onClose}>
      <section className="modal" role="dialog" aria-modal="true" aria-label={title} onMouseDown={(event) => event.stopPropagation()}>
        <header className="modal-head">
          <div>
            <h2>{title}</h2>
            {subtitle ? <p>{subtitle}</p> : null}
          </div>
          <button className="icon-btn" type="button" onClick={onClose} aria-label="Close">×</button>
        </header>
        {children}
      </section>
    </div>
  )
}
