import dayjs from 'dayjs/esm';
import { IRoute } from 'app/entities/route/route.model';

export interface IRouteSchedule {
  id: number;
  code?: string | null;
  description?: string | null;
  departure?: dayjs.Dayjs | null;
  arrival?: dayjs.Dayjs | null;
  route?: IRoute | null;
}

export type NewRouteSchedule = Omit<IRouteSchedule, 'id'> & { id: null };
