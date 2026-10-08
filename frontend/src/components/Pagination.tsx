type Props = { page: number; totalPages: number; loading?: boolean; onChange: (page: number) => void }

function Pagination({ page, totalPages, loading = false, onChange }: Props) {
  return (
    <div className="pagination" aria-label="Pagination">
      <button disabled={loading || page === 0} onClick={() => onChange(page - 1)} type="button">Previous</button>
      <span>Page {page + 1} of {Math.max(totalPages, 1)}</span>
      <button disabled={loading || page + 1 >= totalPages} onClick={() => onChange(page + 1)} type="button">Next</button>
    </div>
  )
}

export default Pagination
