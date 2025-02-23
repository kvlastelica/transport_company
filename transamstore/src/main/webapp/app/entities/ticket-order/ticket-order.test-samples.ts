import dayjs from 'dayjs/esm';

import { ITicketOrder, NewTicketOrder } from './ticket-order.model';

export const sampleWithRequiredData: ITicketOrder = {
  id: 21672,
  placedDate: dayjs('2025-02-22T15:52'),
  status: 'COMPLETED',
  code: 'schlep fax',
};

export const sampleWithPartialData: ITicketOrder = {
  id: 29552,
  placedDate: dayjs('2025-02-22T22:47'),
  status: 'COMPLETED',
  code: 'doing mid midst',
  invoiceId: 7122,
};

export const sampleWithFullData: ITicketOrder = {
  id: 24454,
  placedDate: dayjs('2025-02-22T20:33'),
  status: 'PENDING',
  code: 'next phooey',
  invoiceId: 2549,
};

export const sampleWithNewData: NewTicketOrder = {
  placedDate: dayjs('2025-02-23T14:30'),
  status: 'PENDING',
  code: 'reproachfully goose snowplow',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
