import { IVehicle, NewVehicle } from './vehicle.model';

export const sampleWithRequiredData: IVehicle = {
  id: 7486,
  code: 'snack',
  description: 'who hammock scent',
  format: 'VAN',
};

export const sampleWithPartialData: IVehicle = {
  id: 29101,
  code: 'nicely',
  description: 'gadzooks',
  format: 'PLANE',
};

export const sampleWithFullData: IVehicle = {
  id: 12947,
  code: 'eek boo broken',
  description: 'busily',
  format: 'VAN',
};

export const sampleWithNewData: NewVehicle = {
  code: 'tremendously glisten',
  description: 'but essence',
  format: 'SHIP',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
