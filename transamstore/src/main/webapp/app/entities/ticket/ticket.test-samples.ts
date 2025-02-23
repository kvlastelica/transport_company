import { ITicket, NewTicket } from './ticket.model';

export const sampleWithRequiredData: ITicket = {
  id: 12568,
  name: 'actual',
  price: 25955.5,
  productSize: 'PARCEL_L',
};

export const sampleWithPartialData: ITicket = {
  id: 15478,
  name: 'although',
  description: 'why duh astride',
  price: 24163.08,
  productSize: 'PARCEL_L',
};

export const sampleWithFullData: ITicket = {
  id: 14664,
  name: 'although aboard',
  description: 'march at',
  price: 13244.89,
  productSize: 'PASSENGER_KID',
  image: '../fake-data/blob/hipster.png',
  imageContentType: 'unknown',
};

export const sampleWithNewData: NewTicket = {
  name: 'wee devoted',
  price: 12545.99,
  productSize: 'PARCEL_M',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
