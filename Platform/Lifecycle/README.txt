Lifecycle project provides life cycle definition in a declarative style, and it focus on life cycle non functional requirements at following areas:

1. Implicit life cycle service for business object.

1.1 Hiding state transition validation from application developer perspective.

Including: 

1.1.1 Stand-alone object state transition validation
1.1.2 Independent object state transition validation
1.1.2.1 Child object state transition validation, whose lifecycle is totally covered by parent object.
1.1.2.2 Relative object state transition validation, whose lifecycle is dependent on some other object.

1.2 Hiding setting business object's state indicator operations from both business object client and application developer perspective.
 

2. Implicit life cycle service for long time recoverable process with transient illegal state fix and error handling, such as download process.    