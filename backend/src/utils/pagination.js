// Pagination helper for mongoose queries

class Pagination {
  constructor(query, queryString) {
    this.query = query;
    this.queryString = queryString;
  }

  // Apply pagination
  paginate() {
    const page = parseInt(this.queryString.page) || 1;
    const limit = parseInt(this.queryString.limit) || 20;
    const skip = (page - 1) * limit;

    this.query = this.query.skip(skip).limit(limit);
    this.page = page;
    this.limit = limit;
    this.skip = skip;

    return this;
  }

  // Apply sorting
  sort() {
    if (this.queryString.sort) {
      const sortBy = this.queryString.sort.split(',').join(' ');
      this.query = this.query.sort(sortBy);
    } else {
      this.query = this.query.sort('-createdAt');
    }

    return this;
  }

  // Apply field selection
  select() {
    if (this.queryString.fields) {
      const fields = this.queryString.fields.split(',').join(' ');
      this.query = this.query.select(fields);
    } else {
      this.query = this.query.select('-__v');
    }

    return this;
  }

  // Apply filters
  filter() {
    const queryObj = { ...this.queryString };
    const excludedFields = ['page', 'sort', 'limit', 'fields', 'search'];
    excludedFields.forEach((el) => delete queryObj[el]);

    // Advanced filtering (gte, gt, lte, lt)
    let queryStr = JSON.stringify(queryObj);
    queryStr = queryStr.replace(/\b(gte|gt|lte|lt)\b/g, (match) => `$${match}`);

    this.query = this.query.find(JSON.parse(queryStr));

    return this;
  }

  // Get pagination info
  async getPaginationInfo(Model, filters = {}) {
    const total = await Model.countDocuments(filters);
    const pages = Math.ceil(total / this.limit);

    return {
      page: this.page,
      limit: this.limit,
      total,
      pages,
      hasNextPage: this.page < pages,
      hasPrevPage: this.page > 1,
    };
  }
}

// Helper function to create pagination response
const paginatedResponse = async (Model, query, req) => {
  const features = new Pagination(Model.find(query), req.query)
    .filter()
    .sort()
    .select()
    .paginate();

  const docs = await features.query;
  const pagination = await features.getPaginationInfo(Model, query);

  return { docs, pagination };
};

// Simple pagination for array results
const paginateArray = (array, page = 1, limit = 20) => {
  const startIndex = (page - 1) * limit;
  const endIndex = page * limit;

  const results = array.slice(startIndex, endIndex);
  const total = array.length;
  const pages = Math.ceil(total / limit);

  return {
    data: results,
    pagination: {
      page,
      limit,
      total,
      pages,
      hasNextPage: page < pages,
      hasPrevPage: page > 1,
    },
  };
};

// Cursor-based pagination for large datasets
const cursorPaginate = async (Model, query, cursor, limit = 20) => {
  const filters = { ...query };

  if (cursor) {
    filters._id = { $gt: cursor };
  }

  const docs = await Model.find(filters)
    .sort({ _id: 1 })
    .limit(limit + 1);

  const hasMore = docs.length > limit;
  if (hasMore) docs.pop();

  const nextCursor = hasMore && docs.length > 0 ? docs[docs.length - 1]._id : null;

  return {
    data: docs,
    pagination: {
      nextCursor,
      hasMore,
      limit,
    },
  };
};

module.exports = {
  Pagination,
  paginatedResponse,
  paginateArray,
  cursorPaginate,
};
