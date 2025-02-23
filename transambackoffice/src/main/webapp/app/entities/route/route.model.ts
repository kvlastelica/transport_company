import { IEmployee } from 'app/entities/employee/employee.model';
import { IVehicle } from 'app/entities/vehicle/vehicle.model';

export interface IRoute {
  id: number;
  code?: string | null;
  description?: string | null;
  start?: string | null;
  destination?: string | null;
  passengerCapacity?: number | null;
  parcelTotalWeight?: number | null;
  employees?: IEmployee[] | null;
  vehicles?: IVehicle[] | null;
}

export type NewRoute = Omit<IRoute, 'id'> & { id: null };
